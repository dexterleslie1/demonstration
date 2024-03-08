let languageSwitchTrigger = document.querySelector("#languageSwitchTrigger")
languageSwitchTrigger.addEventListener("click", () => {
    let x = document.querySelector(".ul-lang-switch")
    if (x.style.display === "none" || !x.style.display) {
        x.style.display = "block";
    } else {
        x.style.display = "none";
    }
})

// 点击导航栏的 .nav-ul>li
let lis = document.querySelectorAll(".nav-ul>li")
lis.forEach((e, index) => {
    e.addEventListener("click", (event) => {
        lis.forEach((li) => {
            if (li != event.target)
                li.classList.remove("expand")
        })
        lis[index].classList.toggle("expand")
    })
})

// 点击 feedback
let feedback = document.querySelector(".feedback")
let feedbackSubmitPanel = document.querySelector(".feedback-submit-panel")
let feebackDispalyOrigin
feedback.addEventListener("click", () => {
    feebackDispalyOrigin = feedback.style.display
    feedback.style.display = "none"
    feedbackSubmitPanel.style.display = "block"
})
feedbackSubmitPanel.querySelector(".close").addEventListener("click", () => {
    feedback.style.display = feebackDispalyOrigin
    feedbackSubmitPanel.style.display = "none"
})
