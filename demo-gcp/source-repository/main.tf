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

data "google_project" "demo_google_project" {
    
}

# resource "google_project_iam_member" "demo_google_project_iam_member" {
#     for_each = toset(["roles/resourcemanager.projectIamAdmin"])
#     project = var.my_project_id
#     role = each.key
#     member = "serviceAccount:${data.google_project.demo_google_project.number}-compute@developer.gserviceaccount.com"
# }

# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/sourcerepo_repository
# NOTE: 需要使用enable-service先启用api，否则无法创建sourcerepo
# NOTE: 需要通过gcloud授权service account权限，否则报错，https://stackoverflow.com/questions/67907211/how-to-resolve-googleapi-error-403-the-caller-does-not-have-permission-forbi
# gcloud projects add-iam-policy-binding xyfront-357718 \
# --member=serviceAccount:<YOUR SERVICE ACCOUNT> \
# --role=roles/resourcemanager.projectIamAdmin
# NOTE: 报告错误无法创建sourcerepo
resource "google_sourcerepo_repository" "demo_google_sourcerepo_repository" {
    name = "demo-google-sourcerepo-repository"
}

