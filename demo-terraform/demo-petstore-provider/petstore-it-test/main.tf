terraform {
  required_providers {
    petstore = {
      source  = "my-pet/petstore"
      version = "~> 1.0"
    }
  }
}

provider "petstore" {
  host = "localhost"
}

resource "petstore_pet" "pet" {
  name = "my-pet1"
  age  = 29
}
