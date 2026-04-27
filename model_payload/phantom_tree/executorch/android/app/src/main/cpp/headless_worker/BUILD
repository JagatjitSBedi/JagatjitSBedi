load("@rules_cc//cc:defs.bzl", "cc_binary")

cc_binary(
    name = "npu_worker",
    srcs = ["npu_worker.cc"],
    deps = [
        "//executorch/extension/llm/runner:text_generation_runner",
    ],
    linkopts = ["-pie", "-llog"],
)
