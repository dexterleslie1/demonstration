provider "google" {
  project = "xyfront-357718"

  # region和zone列表
  # https://cloud.google.com/compute/docs/regions-zones
  region = "asia-east2"
  zone   = "asia-east2-a"
}

# 启用source repository api
# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/google_project_service
# 查询api对应的service名称
# https://console.cloud.google.com/apis/library

# NOTE: 需要手动启用 cloud resource manager api
# https://console.cloud.google.com/apis/library/cloudresourcemanager.googleapis.com?project=xyfront-357718
resource "google_project_service" "demo_google_project_service" {
  service            = "sourcerepo.googleapis.com"
  disable_on_destroy = false

  provisioner "local-exec" {
    command = "sleep 30"
  }
}
