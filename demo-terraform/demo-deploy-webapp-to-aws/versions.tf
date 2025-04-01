terraform {
    required_version = ">= 0.15"
    required_providers {
      aws = {
        source = "hashicorp/aws"
        version = "~> 5.17"
      }
      random = {
        source = "hashicorp/random"
        version = "~> 3.5"
      }
      cloudinit = {
        source = "hashicorp/cloudinit"
        version = "~> 2.3"
      }
    }
}
