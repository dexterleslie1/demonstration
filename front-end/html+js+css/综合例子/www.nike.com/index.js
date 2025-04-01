let el = document.querySelector("header .bar2 .more-or-close")
el.addEventListener("click", () => {
    el.classList.toggle("expand")
    document.querySelector("header .bar2 .center").classList.toggle("expand")
})

let elList = document.querySelectorAll("header .bar2 .nav-container")
elList.forEach((el) => {
    el.addEventListener("click", () => {
        elList.forEach((elInternal) => {
            if (elInternal != el)
                elInternal.closest("li").classList.remove("expand")
        })
        let elLi = el.closest("li")
        elLi.classList.toggle("expand")
    })
})