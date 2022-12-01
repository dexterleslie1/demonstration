//
// Created by dexterleslie on 22-11-29.
//

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

int main() {
    char *envVariableName = "testenv";
    char *envValue = getenv(envVariableName);
    assert(envValue == NULL);

    envVariableName = "PATH";
    envValue = getenv(envVariableName);
    printf("environment varialble %s=%s\n", envVariableName, envValue);

    return 0;
}
