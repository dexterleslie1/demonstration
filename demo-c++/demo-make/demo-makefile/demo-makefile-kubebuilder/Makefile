LOCALBIN ?= $(shell pwd)/bin
CONTROLLER_GEN ?= $(LOCALBIN)/controller-gen
CONTROLLER_TOOLS_VERSION ?= v0.13.0

.PHONY: all
all: test

$(LOCALBIN):
	mkdir -p $(LOCALBIN)

.PHONY: controller-gen

# controller-gen 依赖于 $(CONTROLLER_GEN) 目标
controller-gen: $(CONTROLLER_GEN)

# $(CONTROLLER_GEN) 依赖于 $(LOCALBIN) 目标
$(CONTROLLER_GEN): $(LOCALBIN)
	test -s $(CONTROLLER_GEN) && $(CONTROLLER_GEN) --version | grep -q $(CONTROLLER_TOOLS_VERSION) || \
	GOBIN=$(LOCALBIN) go install sigs.k8s.io/controller-tools/cmd/controller-gen@$(CONTROLLER_TOOLS_VERSION)

.PHONY: test
# test 依赖于 controller-gen 目标
test: controller-gen
	@echo "触发下载controller-gen文件"
	@echo -n "controller-gen --version命令执行结果: " && $(CONTROLLER_GEN) --version
