# Tests

- [Tests](#tests)
  - [Introduction](#introduction)
  - [Contract Test](#contract-test)
  - [Smoke Test](#smoke-test)


## Introduction

The Contract and Smoke tests are to ensure that the built image and the deployed resource are working as expected.

## Contract Test

This can be run locally or as part of the build pipeline. To run the contract tests use the following command:

    make run-contract-test

## Smoke Test

The purpose of the smoke test is to ensure deployment readiness of the deployed image. The tests are configured to run with different levels of authenticaton depending on the environment.

Environments:
  - Dev (Non-production)
  - demo
  - sg (Staging)
  - perf (Performance)
  - prod (Production)

To run the smoke test, use the following command:

    make run-smoke-tests

In the *Dev*, *Test* and *Perf* environments the smoke tests are configured to use the mock token `MOCK_POSTCODE_API_ACCESS_TOKEN`

For the *Prod*, *Demo* and *Staging* environments the smoke tests uses a real token obtained from the authentication API.
