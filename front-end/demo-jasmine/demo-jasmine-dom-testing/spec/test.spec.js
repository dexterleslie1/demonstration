describe("测试套件", function () {
    it("步骤1", function () {
        expect(add(1, 5)).toBe(6);
    });

    it('步骤2', function () {
        expect(window).not.toEqual(null)
        expect(document).not.toEqual(null)
        expect(document.body).not.toEqual(null)
        let elementDiv = document.createElement('div')
        elementDiv.id = 'my-id-for-testing'
        document.body.appendChild(elementDiv)

        elementDiv = document.getElementById('my-id-for-testing')
        expect(elementDiv).not.toEqual(null)
    })
});