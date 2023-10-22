variable "vsphere_password" {
  type        = string
  sensitive   = true
  description = "vCenter密码"
}
variable "ssh_user" {
  type    = string
  default = "root"
}
variable "ssh_password" {
  type    = string
  default = "Root@123"
}
variable "demo_nfs_ip" {
  type = string
  default = "192.168.1.186"
}
variable "demo_ansible_ip" {
  type = string
  default = "192.168.1.187"
}
variable "demo_master_ip" {
  type    = string
  default = "192.168.1.188"
}
variable "demo_node_ips" {
  type = list(string)
  default = ["192.168.1.189", "192.168.1.190", "192.168.1.191", "192.168.1.192"]
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
variable "vsphere_template_centos7" {
  type = string
  default = "my-template-centOS7"
}
variable "ipv4_gateway" {
  type    = string
  default = "192.168.1.1"
}
variable "vm_folder" {
  type    = string
  default = "vm/private"
}
