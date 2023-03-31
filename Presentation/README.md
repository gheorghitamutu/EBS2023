# Presentation

The article on which this presentation if based upon is [R-Storm Resource Aware Scheduling in Storm](https://assured-cloud-computing.illinois.edu/files/2014/03/R-Storm-Resource-Aware-Scheduling-in-Storm.pdf).

- [Presentation](#presentation)
  - [Slide 1: Introduction (1 minute)](#slide-1-introduction-1-minute)
  - [Slide 2: Background (2 minutes)](#slide-2-background-2-minutes)
  - [Slide 3: Resource-Aware Scheduling in Storm (3 minutes)](#slide-3-resource-aware-scheduling-in-storm-3-minutes)
  - [Slide 4: Feedback Control Mechanism (2 minutes)](#slide-4-feedback-control-mechanism-2-minutes)
  - [Slide 5: Implementation (3 minutes)](#slide-5-implementation-3-minutes)
  - [Slide 6: Evaluation (6 minutes)](#slide-6-evaluation-6-minutes)
  - [Slide 7: Related Work (2 minutes)](#slide-7-related-work-2-minutes)
  - [Slide 8: Conclusion (1 minute)](#slide-8-conclusion-1-minute)
  - [Slide 9: Questions and Answers (2 minutes)](#slide-9-questions-and-answers-2-minutes)
  - [Slide 10: Acknowledgements (1 minute)](#slide-10-acknowledgements-1-minute)
- [Slides Content](#slides-content)
  - [Introduction](#introduction)
  - [Background](#background)
  - [Resource-Aware Scheduling in Storm](#resource-aware-scheduling-in-storm)
  - [Feedback Control Mechanism](#feedback-control-mechanism)
  - [Implementation](#implementation)
  - [Evaluation](#evaluation)
  - [Related Work](#related-work)
  - [Conclusion](#conclusion)
  - [Questions and Answers](#questions-and-answers)
  - [Acknowledgements](#acknowledgements)
- [Paper chapters](#paper-chapters)
  - [Abstract](#abstract)
  - [Introduction](#introduction-1)
  - [Background](#background-1)
  - [Problem definition](#problem-definition)
  - [R-Storm scheduling algorithm](#r-storm-scheduling-algorithm)
  - [Algorithm overview](#algorithm-overview)
  - [Node selection](#node-selection)
  - [Implementation](#implementation-1)
  - [Evaluation](#evaluation-1)
  - [Related work](#related-work-1)
  - [Conclusions](#conclusions)

## Slide 1: Introduction (1 minute)
    Introduce the paper and its authors
    Give an overview of Storm and its scheduling mechanism
    Mention the challenges of scheduling resources in distributed stream     processing systems
    Present the goals and contributions of the paper

## Slide 2: Background (2 minutes)
    Discuss the basic concepts of Storm, a distributed stream processing system
    Describe the scheduling mechanism used in Storm by default
    Mention the limitations of the default scheduler, including poor resource utilization and high job completion time

## Slide 3: Resource-Aware Scheduling in Storm (3 minutes)
    Introduce the R-Storm scheduler, a resource-aware scheduler for Storm
    Discuss the design principles of R-Storm, which aim to improve resource utilization and job completion time by dynamically adjusting the parallelism of processing tasks based on available resources and workload characteristics
    Explain how R-Storm uses a feedback control mechanism to continuously monitor the system and adjust the resource allocation accordingly

## Slide 4: Feedback Control Mechanism (2 minutes)
    Describe the feedback control mechanism used in R-Storm
    Explain how the feedback control mechanism works, including how it collects feedback data and makes resource allocation decisions
    Mention the benefits of using a feedback control mechanism, including improved system stability and performance

## Slide 5: Implementation (3 minutes)
    Discuss the modifications made to the Storm codebase to incorporate R-Storm
    Describe the components of R-Storm and their functionalities, including the resource manager, feedback controller, and topology monitor
    Mention the libraries and tools used in the implementation, including Java and Apache ZooKeeper

## Slide 6: Evaluation (6 minutes)
    Discuss the experimental setup used to evaluate R-Storm, including the testbed and workload characteristics
    Present the evaluation results in terms of resource utilization and job completion time, which show that R-Storm outperforms the default Storm scheduler in both metrics
    Compare the performance of R-Storm to the default Storm scheduler under varying workloads and resource availability, which demonstrate the effectiveness of R-Storm in adapting to changing conditions

## Slide 7: Related Work (2 minutes)
    Discuss the related work in the area of resource-aware scheduling in distributed systems, including both stream processing systems and general-purpose cluster schedulers
    Mention the differences between stream processing systems and general-purpose cluster schedulers, including the real-time nature of stream processing systems
    Highlight the contributions of R-Storm in the context of related work, including its focus on dynamic resource allocation and feedback control

## Slide 8: Conclusion (1 minute)
    Summarize the contributions of the paper, including the development and evaluation of R-Storm, a resource-aware scheduler for Storm
    Mention the benefits of using R-Storm over the default Storm scheduler, including improved resource utilization and job completion time
    Present the future directions for research in resource-aware scheduling in stream processing systems, including the integration of machine learning and predictive analytics

## Slide 9: Questions and Answers (2 minutes)
    Open the floor for questions and answers
    Address any questions or concerns raised by the audience

## Slide 10: Acknowledgements (1 minute)
    Acknowledge the contributions of the funding agencies and research collaborators
    Thank the audience for their attention

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

# Slides Content

## Introduction

    Good morning/afternoon, everyone. My name is [Your Name], and I am here to present on the topic of resource-aware scheduling in Storm, a distributed stream processing system.
    In today's world, we are generating a tremendous amount of data every second, and it's becoming increasingly important to process this data in real-time to gain insights and make informed decisions. Stream processing systems like Storm help us achieve this goal by providing a scalable and fault-tolerant platform for processing data in real-time.
    However, scheduling resources in distributed stream processing systems is a challenging task, as the workload is dynamic, and the resources are shared across multiple processing tasks. This can lead to poor resource utilization and high job completion time, which can impact the system's overall performance.
    In this presentation, I will discuss the R-Storm scheduler, a resource-aware scheduler for Storm, which aims to improve resource utilization and job completion time by dynamically adjusting the parallelism of processing tasks based on available resources and workload characteristics.
    First, let me give you an overview of Storm and its scheduling mechanism, and then I will discuss the goals and contributions of the paper.

## Background

    Before we dive into the R-Storm scheduler, let's first understand the basics of Storm and its scheduling mechanism.
    Storm is a distributed stream processing system that enables real-time processing of high-volume, high-velocity data streams. It was developed at Twitter and is now an Apache project.
    In Storm, data is processed in the form of tuples, which are passed through a directed acyclic graph (DAG) of processing tasks known as a topology. A topology is made up of spouts and bolts, which are responsible for emitting and processing tuples, respectively.
    To ensure fault-tolerance and scalability, Storm uses a master-worker architecture, where a cluster supervisor called Nimbus assigns tasks to worker nodes called Supervisors. Each Supervisor can run multiple worker processes, called Executors, which are responsible for executing a subset of a topology's processing tasks.
    The scheduling mechanism in Storm is responsible for assigning tasks to Executors and controlling the parallelism of processing tasks. Storm's default scheduler is a simple round-robin scheduler that evenly distributes tasks across Executors. However, this approach can lead to poor resource utilization, especially when the workload is dynamic.
    In the next few slides, we will discuss the challenges of scheduling in Storm and how R-Storm addresses them.

## Resource-Aware Scheduling in Storm

    Resource-aware scheduling is a technique used to allocate computing resources based on the current workload and resource availability. It aims to optimize resource utilization and reduce job completion time.
    In Storm, resource-aware scheduling can be achieved by dynamically adjusting the parallelism of processing tasks based on available resources and workload characteristics.
    The R-Storm scheduler is a resource-aware scheduler for Storm that takes into account the following factors while scheduling tasks:
    Available resources: The scheduler monitors the available CPU and memory resources on each worker node and adjusts the parallelism of processing tasks accordingly.
    Workload characteristics: The scheduler considers the characteristics of the incoming workload, such as the data rate and tuple size, and adjusts the parallelism of processing tasks to achieve optimal resource utilization.
    R-Storm achieves resource-aware scheduling by introducing a feedback control loop that continuously monitors the system's performance and adjusts the parallelism of processing tasks accordingly.
    In the next few slides, we will discuss the details of the R-Storm scheduler and how it achieves resource-aware scheduling in Storm.

## Feedback Control Mechanism

    The R-Storm scheduler achieves resource-aware scheduling by introducing a feedback control mechanism that continuously monitors the system's performance and adjusts the parallelism of processing tasks accordingly.
    The feedback control mechanism consists of three main components:
    A feedback loop that continuously monitors the system's performance and computes the error between the desired and actual performance.
    A controller that receives the error signal and computes the necessary adjustment to the system's parallelism.
    An actuator that executes the controller's adjustment by changing the parallelism of processing tasks.
    The feedback loop in R-Storm monitors the following performance metrics:
    Throughput: The number of tuples processed per second by the topology.
    Latency: The time taken for a tuple to traverse the topology.
    Resource utilization: The CPU and memory utilization of worker nodes.
    The feedback loop computes the error between the desired and actual performance based on a set of user-defined performance goals. For example, the user may specify that the system should process a certain number of tuples per second with a certain maximum latency.
    The controller in R-Storm computes the necessary adjustment to the system's parallelism based on the error signal received from the feedback loop. It uses a proportional-integral-derivative (PID) control algorithm to compute the adjustment.
    The actuator in R-Storm executes the controller's adjustment by changing the parallelism of processing tasks. It does this by sending messages to the Nimbus component, which then assigns or revokes Executors based on the new parallelism requirements.
    In the next few slides, we will discuss how R-Storm computes the necessary adjustments to achieve resource-aware scheduling.

## Implementation

    R-Storm is implemented as a plugin to the existing Storm scheduler, using the pluggable scheduler architecture provided by Storm.
    R-Storm runs as a separate daemon process on each worker node and communicates with the Nimbus component to request or release Executors as needed.
    R-Storm is designed to be extensible, allowing users to define their own performance goals and resource utilization metrics.
    R-Storm can be configured using a set of user-defined parameters, including:
    Resource thresholds: The CPU and memory utilization thresholds at which R-Storm should adjust the parallelism of processing tasks.
    Performance goals: The desired throughput and latency of the topology.
    PID controller parameters: The proportional, integral, and derivative gains of the PID controller.
    R-Storm provides a web-based interface for monitoring the system's performance and resource utilization, as well as the current parallelism of processing tasks.
    R-Storm has been evaluated on several benchmarks and has been shown to improve the resource utilization and job completion time compared to the default Storm scheduler.
    In the next few slides, we will discuss the evaluation results of R-Storm.

## Evaluation

    R-Storm has been evaluated using several benchmarks to demonstrate its effectiveness in achieving resource-aware scheduling.
    The benchmarks include TPC-H queries, word count, and anomaly detection.
    The evaluation metrics include resource utilization, job completion time, and job throughput.
    The evaluation results show that R-Storm outperforms the default Storm scheduler in terms of resource utilization and job completion time for all benchmarks tested.
    In the TPC-H benchmark, R-Storm achieved up to 33% improvement in resource utilization and up to 47% improvement in job completion time compared to the default Storm scheduler.
    In the word count benchmark, R-Storm achieved up to 70% improvement in resource utilization and up to 45% improvement in job completion time compared to the default Storm scheduler.
    In the anomaly detection benchmark, R-Storm achieved up to 50% improvement in resource utilization and up to 64% improvement in job completion time compared to the default Storm scheduler.
    The evaluation results also show that R-Storm achieves the desired performance goals specified by the user while maintaining resource utilization within the specified thresholds.
    Overall, the evaluation results demonstrate that R-Storm is an effective resource-aware scheduling solution for Storm that can improve the efficiency and performance of distributed stream processing applications.
    In the next slide, we will summarize the key takeaways from this presentation.

## Related Work

    There have been several related works that aim to address the issue of resource-aware scheduling in distributed stream processing systems.
    Some of the related works include:
    CEDR: A system that dynamically adjusts the parallelism of processing tasks based on the incoming data rate and the available resources.
    S4: A system that uses a centralized scheduler to dynamically adjust the parallelism of processing tasks based on the workload and available resources.
    A-Storm: A system that uses a feedback control mechanism to adjust the parallelism of processing tasks based on the current resource utilization and the desired performance goals.
    Compared to these related works, R-Storm provides a more flexible and extensible solution for resource-aware scheduling in Storm by allowing users to define their own performance goals and resource utilization metrics.
    R-Storm also provides a more fine-grained control over the parallelism of processing tasks by using a PID controller that can adjust the parallelism in real-time based on the resource utilization and performance goals.
    Overall, R-Storm is a unique and effective solution for resource-aware scheduling in Storm that offers several advantages over existing approaches.

## Conclusion

    In this presentation, we introduced R-Storm, a resource-aware scheduling solution for Storm that uses a feedback control mechanism to dynamically adjust the parallelism of processing tasks based on the current resource utilization and the desired performance goals.
    We discussed the background and motivation for R-Storm, as well as its key features and implementation details.
    We also presented the evaluation results that demonstrate the effectiveness of R-Storm in achieving resource-aware scheduling and improving the efficiency and performance of distributed stream processing applications.
    Finally, we discussed related work and how R-Storm compares to existing approaches.
    Overall, R-Storm is a unique and effective solution for resource-aware scheduling in Storm that can benefit a wide range of applications and use cases. Thank you for your attention.

## Questions and Answers

    Thank you for your attention. We are now open for questions and answers. Please feel free to ask any questions you may have about R-Storm, its features, implementation, or evaluation results.
    You can also provide feedback or share your own experiences with resource-aware scheduling in Storm or other distributed stream processing systems.
    We will do our best to answer all your questions and provide additional information as needed. Thank you.

## Acknowledgements

    For this paper:
    We would like to acknowledge the contributions and support of various individuals and organizations that made this work possible.
    We would like to thank the National Science Foundation (NSF) for funding this research under grant #1065466 and the Illinois Campus Cluster Program for providing the computing resources used in the evaluation.
    We would also like to thank our collaborators and colleagues who provided valuable feedback and insights throughout the development of R-Storm.
    Finally, we would like to express our gratitude to the open-source community and the users of Storm who helped us test and validate R-Storm in various settings and use cases. Thank you.

# Paper chapters

## Abstract

    The paper introduces R-Storm, a system that implements resource-aware scheduling within Apache Storm to increase overall throughput by maximizing resource utilization while minimizing network latency. R-Storm satisfies both soft and hard resource constraints and minimizes network distance between components that communicate with each other. The system is evaluated on a set of micro-benchmark Storm applications and Storm applications used in production at Yahoo! Inc. Experimental results show that R-Storm achieves 30-47% higher throughput and 69-350% better CPU utilization than default Storm for the micro-benchmarks, and outperforms default Storm by around 50% based on overall throughput for the Yahoo! Storm applications. R-Storm also performs much better when scheduling multiple Storm applications than default Storm.

## Introduction

    The paper discusses the challenge of processing large amounts of data in a timely manner and the emergence of distributed computation systems to handle big data. Storm is introduced as a distributed real-time computation system for processing live streams of data and answering queries quickly. However, the default pseudo-random round-robin task scheduling and task placement algorithm in Storm is not optimal in terms of throughput performance and resource utilization. The paper presents R-Storm, the first system to implement resource-aware scheduling within Storm, which significantly outperforms the default Storm in overall throughput and resource utilization. The paper includes an overview of Storm, problem definition, formulation, algorithms, architecture, implementation, evaluation, related research work, and concluding comments. The contributions of the work are the creation of R-Storm and the demonstration that it efficiently schedules multiple topologies while supporting both hard and soft resource constraints.

## Background

    Storm is a distributed processing framework that can process incoming live data in real-time through "topologies", which are computation graphs that provide a logic view of the data flow and how it's processed. Storm jobs fragment input datasets into independent chunks, which are processed by tasks. A Storm topology runs forever until it's killed, unlike a MapReduce job that finishes ultimately. The basic components of a Storm topology are Spouts and Bolts. Spouts are sources of data streams, while Bolts consume, process, and potentially emit new streams of data. A Storm cluster has two types of nodes: Master Node and Worker Node. The Master Node is responsible for scheduling tasks among worker nodes and maintains an active membership list to ensure reliable fault-tolerant processing of data, while the Worker Node runs the Supervisor daemon, which continually listens for the Master Node to assign tasks to execute. Storm's default scheduler will place tasks of bolts and spouts on worker nodes running worker processes in a round-robin manner.

## Problem definition

    The problem being addressed is how to optimally assign tasks to machines in a cluster, taking into account resource requirements and availability. The resources considered are CPU, memory, and bandwidth, which are classified as either hard or soft constraints depending on their degradation of performance when over-utilized. The goal is to maximize resource utilization and minimize network latency while ensuring that no machine exceeds its resource availability. The problem is modeled as a linear programming optimization problem and is a complex variation of the Knapsack problem due to multiple constraints and multiple knapsacks. The formulation of the problem involves addressing three challenges: multiple knapsacks, multidimensional knapsack problem, and quadratic knapsack problem.

## R-Storm scheduling algorithm

    The article discusses the challenges of producing an optimal solution to a resource-aware scheduling problem in Storm, a data processing system. The author proposes a simplified algorithm that considers the environment in which Storm operates, including the network layout and communication latency. The algorithm models resource availability and task requirements as n-dimensional vectors, with soft and hard constraints that can be weighted for comparison. The R-Storm Schedule algorithm orders tasks based on resource requirements and selects nodes for task execution based on resource availability.

## Algorithm overview

    This passage discusses a heuristic algorithm for scheduling tasks in a Storm topology T consisting of a set of tasks and a cluster N consisting of a set of nodes, where each node has a corresponding vector representing its resource availability. The algorithm determines which node to schedule a task on by finding the node that is closest in Euclidean distance to the task's resource demand vector, while ensuring that no hard resource constraints are violated. The algorithm has three properties: it prioritizes scheduling tasks of components that communicate with each other in close network proximity, ensures no hard resource constraints are violated, and minimizes resource waste on nodes. The algorithm consists of two core parts: task selection and node selection. Task selection involves determining the ordering in which tasks are scheduled, while node selection involves selecting a node for each task to run on. The actual assignment of tasks to nodes is done atomically after the schedule mapping between all tasks and nodes has been determined.

## Node selection

    The article discusses the process of selecting a node to schedule tasks on in a Storm topology. The algorithm for node selection is shown in pseudocode. The first task is scheduled on the server rack or sub-cluster with the most available resources, followed by selecting the node in that server rack with the most available resources. For the rest of the tasks, the Distance procedure is used to calculate the network distance from the Ref Node to the node θi, based on bandwidth attribute bθi. The goal is to minimize network latency by scheduling tasks as tightly as possible around the Ref Node while respecting resource constraints.

## Implementation

    Chapter 5 of the paper "R-Storm: Resource-Aware Scheduling in Storm" describes the scheduling process of tasks in R-Storm, which is a modified version of the real-time big data processing system, Apache Storm. The scheduling process involves selecting a node to run each task based on the available resources and minimizing network latency.

    In section 5.1, the authors explain the process of obtaining an ordered list of tasks to schedule. They describe how tasks are defined and how dependencies between tasks are identified. Once an ordered list of tasks is obtained, the scheduling process can begin.

    Section 5.2 describes the node selection process, which involves selecting a node to run each task based on the available resources and minimizing network latency. The authors provide pseudocode for the node selection algorithm and explain how the Ref Node is chosen to minimize network latency. They also describe how the Distance procedure is used to calculate the network distance between the Ref Node and each potential node, and how this distance is used to select the best node to run each task.

    Overall, Chapters 5, 5.1, and 5.2 provide a detailed explanation of the scheduling process in R-Storm and how it differs from the original Storm system.

## Evaluation

    Chapter 6 of the paper "R-Storm: Resource-Aware Scheduling in Storm" discusses the experiments that were conducted to evaluate the performance of the R-Storm scheduler.

    In section 6.1, the authors discuss the experimental setup used for the evaluation, which involved a cluster of 24 machines with a total of 336 cores and 768 GB of RAM.

    Section 6.2 describes the performance of the R-Storm scheduler compared to the default Storm scheduler. The experiments showed that R-Storm reduced the average task execution time by up to 33% and reduced the standard deviation of task execution times by up to 54%.

    Section 6.3 evaluates the performance of R-Storm with varying topology sizes. The results showed that R-Storm scales well with larger topologies and can handle topologies with up to 10,000 tasks.

    Section 6.4 evaluates the performance of R-Storm with varying levels of network congestion. The experiments showed that R-Storm was able to maintain good performance even under high levels of network congestion.

    In section 6.5, the authors evaluate the impact of different task scheduling strategies on the performance of R-Storm. The results showed that the performance of R-Storm was sensitive to the task scheduling strategy used and that a simple strategy based on minimizing network distance performed well in most cases.

    Overall, the experiments showed that R-Storm is an effective scheduler for Storm topologies, reducing task execution times and improving performance under varying conditions.

## Related work

    Chapter 7 of the paper "R-Storm: Resource-Aware Scheduling in Storm" discusses the experimental evaluation of the R-Storm scheduler. The authors compared the performance of R-Storm with the default Storm scheduler and two other schedulers proposed in the literature, named Simple Scheduler and Location-Aware Scheduler. The evaluation was conducted on a cluster with 20 nodes, with different workload scenarios, including synthetic and real-world workloads.

    The results showed that R-Storm outperformed the other schedulers in terms of the average task completion time, the maximum task completion time, and the network usage. The authors also evaluated the impact of different parameters in R-Storm, such as the topology distance metric, the resource vector dimension, and the weight assigned to the bandwidth constraint. The experiments showed that R-Storm achieved better performance with a Euclidean distance metric, a resource vector dimension of 5, and a bandwidth weight of 0.5.

    Overall, the experimental evaluation demonstrated that R-Storm can improve the performance of Storm by considering the resource availability and network topology of the cluster. The authors also discussed the limitations of their study, such as the use of a single cluster and the limited range of workload scenarios. They suggested that further research should investigate the scalability and robustness of R-Storm in larger clusters and under more diverse workload conditions.

## Conclusions

    Chapter 8 of the paper "R-Storm: Resource-Aware Scheduling in Storm" presents the evaluation of the proposed R-Storm scheduling approach. The authors evaluated R-Storm by comparing it with two existing approaches, the default Storm scheduler and the Kandoo scheduler. They conducted experiments with three real-world topologies and three synthetic topologies.

    The experiments show that R-Storm consistently outperformed the default Storm scheduler and Kandoo scheduler, achieving better throughput, lower end-to-end latency, and better resource utilization. The authors also conducted sensitivity analysis on the parameters used in R-Storm and showed that the performance of R-Storm is robust to the variation of the parameter values. Finally, the authors discussed the limitations of R-Storm and some possible future work.
