data "aws_eks_cluster" "eks" {
  name = "live-leks-cluster"
}

resource "aws_iam_role" "iam_host_role" {
  path = "/"
  name = local.service_account_role_name

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement" : [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated" : "arn:aws:iam::${var.aws_account_id}:oidc-provider/${trimprefix(data.aws_eks_cluster.eks.identity[0].oidc[0].issuer, "https://")}"
        },
        "Action": ["sts:AssumeRole","sts:AssumeRoleWithWebIdentity"],
        "Condition": {
          "StringLike": {
            "${trimprefix(data.aws_eks_cluster.eks.identity[0].oidc[0].issuer, "https://")}:sub": "system:serviceaccount:${var.project_id}*:uec-sf-pc-service-account"
        }
      }
    }
  ]
}
EOF
}


resource "aws_iam_policy" "service_account_policy" {
  name        = local.postcode_service_account_policy_name
  path        = "/"
  description = "Postcode IAM role policies"

  # Terraform's "jsonencode" function converts a
  # Terraform expression result to valid JSON syntax.
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "ec2:*",
          "s3:*",
          "dynamodb:*",
          "ecr:*",
          "lambda:*",
          "es:*",
          "rds:*",
          "iam:*",
          "ecr:BatchGetImage"

        ]
        Effect   = "Allow"
        Resource = "*"
      },
    ]
  })
}

resource "aws_iam_role_policy_attachment" "aws_iam_role_policy_attachment" {
  role       = aws_iam_role.iam_host_role.name
  policy_arn = aws_iam_policy.service_account_policy.arn
  #policy_arn = "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"
}
/*
resource "aws_iam_role_policy_attachment" "aws_iam_role_policy_attachment" {
  role = aws_iam_role.iam_host_role.name
  # policy_arn = aws_iam_policy.service_account_policy.arn
  policy_arn = "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"
}
*/
