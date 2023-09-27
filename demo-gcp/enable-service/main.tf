
variable "my_project_id" {
  type    = string
  default = "xyfront-357718"
}
provider "google" {
  project = var.my_project_id

  # region和zone列表
  # https://cloud.google.com/compute/docs/regions-zones
  region = "asia-east2"
  zone   = "asia-east2-a"
}

locals {
  services = [
    "sourcerepo.googleapis.com",
    "cloudbuild.googleapis.com",
    "run.googleapis.com",
    "iam.googleapis.com",
  ]
}

# 启用source repository api
# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/google_project_service
# 查询api对应的service名称
# https://console.cloud.google.com/apis/library

# NOTE: 需要手动启用 cloud resource manager api
# https://console.cloud.google.com/apis/library/cloudresourcemanager.googleapis.com?project=xyfront-357718
resource "google_project_service" "demo_google_project_service" {
  for_each = toset(local.services)
  service  = each.key
  project  = var.my_project_id
  # disable_on_destroy = false

  provisioner "local-exec" {
    command = "sleep 30"
  }

  provisioner "local-exec" {
    when = destroy
    command = "sleep 15"
    
  }
}
