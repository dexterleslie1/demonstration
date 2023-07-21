describe('My Demo1', function () {
    it('Find elements', function (browser) {
        browser
            .navigateTo('https://www.baidu..com')

        browser.element.find('#kw')
            .setValue('abc')

        browser.element.find('#su').click()

        browser.pause(3000)
    });
});