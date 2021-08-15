package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.aws.accounts.Account;

/**
 * {@link ARN Amazon Resource Names (ARNs)} uniquely identify AWS resources.
 * <p>
 * We require an {@link ARN ARN} when you need to specify a resource unambiguously across all of AWS,
 * such as in IAM policies, Amazon Relational Database Service (Amazon RDS) tags, and API calls.
 * <p>
 * The Service Authorization Reference lists the ARNs that you can use in IAM policies.
 * https://docs.aws.amazon.com/service-authorization/latest/reference/
 * <p>
 * The following are the general formats for {@link ARN ARNs}.
 * The specific formats depend on the resource.
 * <p>
 * To use an {@link ARN ARN}, replace the italicized text with the resource-specific information.
 * Be aware that the ARNs for some resources omit the Region, the account ID, or both the Region and the account ID.
 * <p>
 *
 * <pre>
 *     arn:partition:service:region:account-id:resource-id
 * arn:partition:service:region:account-id:resource-type/resource-id
 * arn:partition:service:region:account-id:resource-type:resource-id
 * </pre>
 * https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html
 */
public class ARN
{
    /**
     * The partition in which the resource is located.
     * A partition is a group of {@link io.nanovc.agentsim.aws.regions.Region AWS Regions}.
     * Each {@link Account AWS account } is scoped to one partition.
     * <p>
     * The following are the supported partitions:
     * <ul>
     *     <li>aws -AWS Regions</li>
     *     <li>aws-cn - China Regions</li>
     *     <li>aws-us-gov - AWS GovCloud (US) Regions</li>
     * </ul>
     */
    public String partition;

    /**
     * The service namespace that identifies the AWS product.
     * <p>
     * For example, s3 for Amazon S3.
     * To find a service namespace, open the Service Authorization Reference, open the page for the service,
     * and find the phrase "service prefix" in the first sentence.
     * <p>
     * For example, the following text appears in the first sentence on the page for Amazon S3:
     * <p>
     * (service prefix: s3)
     */
    public String service;

    /**
     * The Region code.
     * <p>
     * For example, us-east-2 for US East (Ohio).
     * <p>
     * For the list of Region codes, see Regional endpoints.
     * <p>
     * https://docs.aws.amazon.com/general/latest/gr/rande.html#regional-endpoints
     */
    public String region;

    /**
     * The ID of the {@link Account AWS account } that owns the resource,
     * without the hyphens.
     * For example, 123456789012.
     *
     * A 12-digit number, such as 123456789012, that uniquely identifies an AWS account.
     * Many AWS resources include the account ID in their Amazon Resource Names ({@link ARN ARNs}).
     * The account ID portion distinguishes resources in one account from the resources in another account.
     * If you are an IAM user, you can sign in to the AWS Management Console using either the account ID or account alias.
     * https://docs.aws.amazon.com/general/latest/gr/acct-identifiers.html
     */
    public String accountID;

    /**
     * The resource identifier.
     * <p>
     * This part of the {@link ARN ARN} can be the name or ID of the resource or a resource path.
     * <p>
     * For example, user/Bob for an IAM user or instance/i-1234567890abcdef0 for an EC2 instance.
     * <p>
     * Some resource identifiers include a parent resource (sub-resource-type/parent-resource/sub-resource)
     * or a qualifier such as a version (resource-type:resource-name:qualifier).
     * <p>
     * <h2>Paths in ARNs</h2>
     * Resource ARNs can include a path.
     * For example, in Amazon S3, the resource identifier is an object name that can include slashes (/) to form a path.
     * Similarly, IAM user names and group names can include paths.
     * <p>
     * Paths can include a wildcard character, namely an asterisk (*).
     * For example, if you are writing an IAM policy, you can specify all IAM users that have the path product_1234 using a wildcard as follows:
     * <p>
     * arn:aws:iam::123456789012:user/Development/product_1234/*
     * <p>
     * Similarly, you can specify user/* to mean all users or group/* to mean all groups, as in the following examples:
     * <p>
     * "Resource":"arn:aws:iam::123456789012:user/*"
     * "Resource":"arn:aws:iam::123456789012:group/*"
     * The following example shows ARNs for an Amazon S3 bucket in which the resource name includes a path:
     * <p>
     * arn:aws:s3:::my_corporate_bucket/*
     * arn:aws:s3:::my_corporate_bucket/Development/*
     * <h2>Incorrect wildcard usage</h2>
     * <p>
     * You cannot use a wildcard in the portion of the ARN that specifies the resource type,
     * such as the term user in an IAM ARN.
     * For example, the following is not allowed.
     * <p>
     * arn:aws:iam::123456789012:u*   <== not allowed
     */
    public String resourceID;
}
