
import BntBox from './index.vue'
let defaultOptions = {
	show:true
};
let body = document.body,
	bindPhone = document.createElement('div')
	bindPhone.setAttribute('id', 'HFBntBox-id')
	body.appendChild(bindPhone)
// #ifndef VUE3
import Vue from 'vue'
const VueComponent = Vue.extend(BntBox);
const BntBoxConfirm = function (options={}) {
	const vm = new VueComponent().$mount('#HFBntBox-id');
	Object.assign(vm,defaultOptions , options);
	document.body.appendChild(vm.$el);
	return 
};
// #endif

// #ifdef VUE3
import { createApp,h } from 'vue'
const VueComponent = createApp({
	render: () => {
      return h(BntBox)
    }})
const BntBoxConfirm = function (options={}) {
	
	const vm = VueComponent.mount('#HFBntBox-id');
	console.log("vm",vm)
	// Object.assign(vm,defaultOptions , options);
	document.body.appendChild(vm.$el);
	return 
};
	
// #endif




export default BntBoxConfirm;