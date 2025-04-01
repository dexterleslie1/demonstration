variable "ucloud_public_key" {
  type    = string
}
variable "ucloud_private_key" {
  type    = string
}
variable "ucloud_project_id" {
  type    = string
  default = "org-24181"
}
variable "ucloud_region" {
  type    = string
  default = "cn-gd"
}

terraform {
  required_providers {
    ucloud = {
      source  = "ucloud/ucloud"
      version = "~>1.37.0"
    }
  }
}

provider "ucloud" {
  public_key  = var.ucloud_public_key
  private_key = var.ucloud_private_key
  project_id  = var.ucloud_project_id
  region      = var.ucloud_region
}

# resource "ucloud_vpc" "demo-vpc" {
#   name        = "demo-vpc"
#   cidr_blocks = ["192.168.1.0/24"]
# }

# Query default security group
data "ucloud_security_groups" "default" {
  type = "recommend_web"
}

# Query normal image
data "ucloud_images" "normal" {
  name_regex = "^CentOS 8.4 64"
  image_type = "base"
}

# Create normal instance
resource "ucloud_instance" "normal" {
  availability_zone = "cn-gd-02"
  image_id          = data.ucloud_images.normal.images[0].id
  instance_type     = "n-basic-2"
  root_password     = "Root@123"
  name              = "demo-centos8"
  boot_disk_type    = "cloud_ssd"
  charge_type       = "dynamic"

  # the default Web Security Group that UCloud recommend to users
  security_group = data.ucloud_security_groups.default.security_groups[0].id

  # create cloud data disk attached to instance
  data_disks {
    size = 50
    type = "cloud_ssd"
  }
  delete_disks_with_instance = true
}
