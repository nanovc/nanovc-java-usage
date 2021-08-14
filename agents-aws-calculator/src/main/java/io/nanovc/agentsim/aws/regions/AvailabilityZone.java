package io.nanovc.agentsim.aws.regions;

import io.nanovc.agentsim.aws.AWSConceptBase;
import io.nanovc.agentsim.aws.NamedAWSConceptBase;

/**
 * An {@link AvailabilityZone Availability Zone (AZ)} is one or more discrete data centers with redundant power, networking, and connectivity in an {@link Region AWS Region}.
 * {@link AvailabilityZone AZs} give customers the ability to operate production applications and databases that are more highly available,
 * fault tolerant, and scalable than would be possible from a single data center.
 * All {@link AvailabilityZone AZs} in an {@link Region AWS Region} are interconnected with high-bandwidth,
 * low-latency networking, over fully redundant, dedicated metro fiber providing high-throughput, low-latency networking between {@link AvailabilityZone AZs}.
 * All traffic between {@link AvailabilityZone AZs} is encrypted.
 * The network performance is sufficient to accomplish synchronous replication between {@link AvailabilityZone AZs}.
 * {@link AvailabilityZone AZs} make partitioning applications for high availability easy.
 * If an application is partitioned across {@link AvailabilityZone AZs},
 * companies are better isolated and protected from issues such as power outages, lightning strikes, tornadoes, earthquakes, and more.
 * {@link AvailabilityZone AZs} are physically separated by a meaningful distance, many kilometers, from any other {@link AvailabilityZone AZ},
 * although all are within 100 km (60 miles) of each other.
 */
public class AvailabilityZone extends NamedAWSConceptBase
{
}
