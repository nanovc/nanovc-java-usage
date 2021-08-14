package io.nanovc.agentsim.aws.regions;

import io.nanovc.agentsim.aws.NamedAWSConceptBase;

/**
 * AWS has the concept of a {@link Region}, which is a physical location around the world where we cluster data centers.
 * We call each group of logical data centers an {@link AvailabilityZone Availability Zone}.
 * Each {@link Region AWS Region} consists of multiple, isolated, and physically separate {@link AvailabilityZone AZs} within a geographic area.
 * Unlike other cloud providers, who often define a region as a single data center,
 * the multiple {@link AvailabilityZone AZ} design of every {@link Region AWS Region} offers advantages for customers.
 * Each {@link AvailabilityZone AZ} has independent power, cooling, and physical security and is connected via redundant, ultra-low-latency networks.
 * AWS customers focused on high availability can design their applications to run in multiple AZs to achieve even greater fault-tolerance.
 * AWS infrastructure {@link Region Regions} meet the highest levels of security, compliance, and data protection.
 * <p>
 * <br>
 * AWS provides a more extensive global footprint than any other cloud provider,
 * and to support its global footprint and ensure customers are served across the world,
 * AWS opens new {@link Region Regions} rapidly.
 * AWS maintains multiple geographic {@link Region Regions},
 * including {@link Region Regions} in North America, South America, Europe, China, Asia Pacific, South Africa, and the Middle East.
 * <p>
 * <br>
 * https://aws.amazon.com/about-aws/global-infrastructure/regions_az/
 */
public class Region extends NamedAWSConceptBase
{
}
