package Lab05;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ColorSerializer extends Serializer<Color> {

    @Override
    public void write(Kryo kryo, Output output, Color object) {
        // TODO Auto-generated method stub
        output.writeInt(object.getRGB());

    }

    @Override
    public Color read(Kryo kryo, Input input, Class<Color> type) {
        // TODO Auto-generated method stub
        return new Color(input.readInt());
    }
}
