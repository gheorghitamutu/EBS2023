package org.project.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.Logger;
import org.project.spouts.fromAMQP.SimplePublicationSpout;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

import static org.project.cofiguration.GlobalConfiguration.*;

public class ConnectionManager implements AutoCloseable  {
    private static final org.apache.log4j.Logger LOG = Logger.getLogger(ConnectionManager.class);

    private static ConnectionManager instance;
    private ConnectionFactory factory;
    private Connection connection;

    private void Connect() {
        this.factory = new ConnectionFactory() {
            public void configureSocket(Socket socket) throws IOException {
                socket.setTcpNoDelay(false);
                socket.setReceiveBufferSize(20 * 1024);
                socket.setSendBufferSize(20 * 1024);
            }
        };
        this.factory.setHost(AMQP_HOST);
        this.factory.setPort(AMQP_PORT);
        this.factory.setUsername(AMQP_USERNAME);
        this.factory.setPassword(AMQP_PASSWORD);
        this.factory.setVirtualHost(AMQP_VHOST);
        try {
            this.connection = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private ConnectionManager() {
        Connect();
    }

    public Channel GetChannel(int prefetchCount, String exchangeName, String queueName, String routingKey) {
        if (this.connection == null) {
            Connect();
        }

        try {
            var channel = this.connection.createChannel();

            channel.confirmSelect();
            channel.basicQos(prefetchCount);
            channel.exchangeDeclare(exchangeName, "direct");
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, exchangeName, routingKey);

            return channel;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        } else if (instance.connection == null || !instance.connection.isOpen()) {
            instance = new ConnectionManager();
        }

        return instance;
    }

    @Override
    public void close() throws Exception {
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (IOException e) {
            LOG.warn("Error closing AMQP connection: ", e);
            throw e;
        }
    }
}
