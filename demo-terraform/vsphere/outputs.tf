output "env_info" {
  value = {
    demo_vm1_ip   = vsphere_virtual_machine.demo_vm1.default_ip_address,
    demo_vm1_name = vsphere_virtual_machine.demo_vm1.name
  }
}
