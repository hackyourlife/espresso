suite = {
  "mxversion" : "5.175.4",
  "name" : "espresso",
  "versionConflictResolution" : "latest",

  "javac.lint.overrides" : "none",

  "imports" : {
    "suites" : [
      {
        "name" : "vmx86",
        "version" : "5d8a4815ca98b85deba21cb370ccbe672bd54692",
        "urls" : [
          {"url" : "https://github.com/pekd/vmx86", "kind" : "git"},
        ]
      },
    ],
  },

  "licenses" : {
    "GPLv3" : {
      "name" : "GNU General Public License, version 3",
      "url" : "https://opensource.org/licenses/GPL-3.0",
    },
    "MPL-1.1" : {
      "name" : "Mozilla Public License 1.1",
      "url" : "https://opensource.org/licenses/MPL-1.1"
    },
    "MPL-2.0" : {
      "name" : "Mozilla Public License 2.0",
      "url" : "https://opensource.org/licenses/MPL-2.0"
    }
  },

  "defaultLicense" : "GPLv3",

  "projects" : {

    "org.graalvm.vm.trcview.arch.ppc" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "vmx86:VMX86_TRCVIEW",
      ],
      "javaCompliance" : "1.8+",
      "workingSets" : "vmx86",
      "license" : "UPL",
    },

  },

  "distributions" : {
    "TRCVIEW_PPC_PLUGIN" : {
      "path" : "build/espresso.jar",
      "sourcesPath" : "build/espresso.src.zip",
      "subDir" : "espresso",
      "dependencies" : [
        "org.graalvm.vm.trcview.arch.ppc",
      ],
      "distDependencies" : [
        "vmx86:VMX86_TRCVIEW",
      ],
      "license" : "GPLv3",
    },

    "TRCVIEW_PPC" : {
      "path" : "build/trcview.jar",
      "sourcesPath" : "build/trcview.src.zip",
      "subDir" : "espresso",
      "mainClass" : "org.graalvm.vm.trcview.ui.MainWindow",
      "dependencies" : [
        "org.graalvm.vm.trcview.arch.ppc",
      ],
      "license" : "GPLv3",
    }
  }
}
