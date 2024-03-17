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
let lis = document.querySelectorAll("nav .left .nav-container .nav-ul>li")
lis.forEach((e, index) => {
    e.addEventListener("click", (event) => {
        lis.forEach((li) => {
            if (!li.contains(event.target))
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

// 点击 section-3 导航展开或者关闭
let elList = document.querySelectorAll("main .container .section-3 .group .entry>div:nth-of-type(1)")
elList.forEach((el) => {
    el.addEventListener("click", () => {
        el.classList.toggle("expand")
    })
})

// section-7
let activeIndex = 0
// 点击左边的 slider arrow 按钮
document.querySelector("main .section-7 .slider-switcher>.arrow:nth-of-type(1)")
    .addEventListener("click", () => {
        if (activeIndex - 1 >= -1)
            activeIndex--
        setActive()
    })
// 点击右边的 slider arrow 按钮
document.querySelector("main .section-7 .slider-switcher>.arrow:nth-of-type(3)")
    .addEventListener("click", () => {
        if (activeIndex + 1 <= 2)
            activeIndex++
        setActive()
    })
setActive()

function setActive() {
    let divList = document.querySelectorAll("main .section-7 .slider-switcher .center-side-slide-switcher>div")
    divList.forEach((divEl) => {
        divEl.classList.remove("active")
    })
    divList[activeIndex + 1].classList.add("active")
    let basis = 60
    let calValue = -activeIndex * 120 + basis

    let itemList = document.querySelectorAll("main .section-7 .slider-container>.item")
    itemList.forEach((item) => {
        item.style.transform = "translateX(" + calValue + "%)"
    })
}

// 点击 nav .more
document.querySelector("nav .more").addEventListener("click", (event) => {
    document.querySelector("nav .right").classList.toggle("active")
    document.querySelector("nav .left").classList.toggle("active")
})

// 解决 .section-5 .collapse-panel translateY问题
let entryList = document.querySelectorAll("main .section-5 .entry")
entryList.forEach((entry) => {
    entry.addEventListener("mouseenter", (event) => {
        let elCollapsePanel = entry.querySelector(".collapse-panel")
        let elImage = elCollapsePanel.querySelector("img:nth-of-type(1)")
        elCollapsePanel.style.transform = "translateY(-" + (elImage.clientHeight + 15) + "px)"
    })
    entry.addEventListener("mouseleave", (event) => {
        let elCollapsePanel = entry.querySelector(".collapse-panel")
        elCollapsePanel.style.transform = "none"
    })
})
