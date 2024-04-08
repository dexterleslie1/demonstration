let el = document.querySelector("header .notification-btn")
el.addEventListener("click", (event) => {
    el.classList.toggle("active")
})
let elNotificationContainer = document.querySelector("header .notification-container")
elNotificationContainer.addEventListener("click", (event) => {
    event.stopPropagation()
})

let elMoreButton = document.querySelector("header .more-button")
elMoreButton.addEventListener("click", () => {
    elMoreButton.classList.toggle("active")
})
let elCompanyNavContainer = document.querySelector("header .company-nav-container")
elCompanyNavContainer.addEventListener("click", (event) => {
    event.stopPropagation()
})

let elAvatar = document.querySelector("header .avatar")
elAvatar.addEventListener("click", () => {
    elAvatar.classList.toggle("active")
})
let elProfileAndFun = document.querySelector("header .profile-and-fun")
elProfileAndFun.addEventListener("click", (event) => {
    event.stopPropagation()
})
