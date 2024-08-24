variable "vsphere_password" {
  type        = string
  sensitive   = true
  description = "vCenter密码"
}
variable "ssh_user" {
  type    = string
  default = "root"
}
variable "demo_vm1_ip" {
  type    = string
  default = "192.168.1.187"
}
variable "vsphere_user" {
  type    = string
  default = "administrator@vsphere.local"
}
variable "vsphere_server" {
  type    = string
  default = "192.168.1.51"
}
variable "vsphere_datacenter" {
  type    = string
  default = "Datacenter"
}
variable "vsphere_datastore" {
  type    = string
  default = "datastore1"
}
variable "vsphere_host" {
  type    = string
  default = "192.168.1.49"
}
variable "vsphere_network" {
  type    = string
  default = "VM Network"
}
variable "vsphere_template" {
  type    = string
  default = "my-template-centOS8"
}
variable "vsphere_template_password" {
  type    = string
  default = "Root@123"
}
variable "ipv4_gateway" {
  type    = string
  default = "192.168.1.1"
}
variable "vm_folder" {
  type    = string
  # 放到private目录
  default = "private"
  # 没有对应目录留空即可
  default = ""
}
