request = function()
  wrk.headers["Connection"] = "Keep-Alive"
  path = "/api/v1/purchaseProduct?userId=1&productId=1&amount=1"
  return wrk.format("POST", path)
end
