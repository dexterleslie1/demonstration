describe('template spec', () => {
  it('passes', () => {
    // cy.visit('/')
    expect(window).not.to.equal(null)
    expect(document).not.to.equal(null)
    expect(document.body).not.to.equal(null)

    let elementDiv = document.createElement('div')
    elementDiv.id = 'my-element-div1'
    document.body.appendChild(elementDiv)

    elementDiv = document.getElementById('my-element-div1')
    expect(elementDiv).not.to.equal(null)
  })
})