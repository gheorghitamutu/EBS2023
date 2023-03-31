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
