variable "my_project_id" {
    type = string
    default = "xyfront-357718"
}

provider "google" {
  project = var.my_project_id

  # region和zone列表
  # https://cloud.google.com/compute/docs/regions-zones
  region = "asia-east2"
  zone   = "asia-east2-a"
}

# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/sourcerepo_repository
# NOTE: 需要使用enable-service先启用api，否则无法创建sourcerepo
# NOTE: 需要通过console > iam 功能授予服务帐号owner角色才能够创建仓库，否则报告403错误。
resource "google_sourcerepo_repository" "demo_google_sourcerepo_repository" {
    name = "demo-google-sourcerepo-repository"
}

