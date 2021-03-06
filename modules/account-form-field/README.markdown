[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
![GitHub top language][top-lenguage-shield]
[![LinkedIn][linkedin-shield]][linkedin-url]

# About The Module

This module provides a custom form field that uses data from selected account to use it as a predefined value, so that the field will be automatically autofillled but the user still an change the data if they want. This field only will fill out the data if an account is selected.


The Account-data-field admits:

 * Labeling
 * Set as required
 * Add Tooltip
 * Validation


## Built With

This Module has been build using the following software:
* [REACT](https://es.reactjs.org/)
* [Clay](https://clayui.com/)

# Getting Started

## Prerequisites

* This module is compatible with **Liferay 7.4**

## Build it
` $ ./gradlew build `
The jar file will be in `build/libs/com.liferay.dynamic.data.mapping.form.field.type.internal.accountdata.field-{version}.jar`.

These are the relevant configuration properties used to build it locally:
- **gradle.properties**:
```
liferay.workspace.product = dxp-7.4-u7
```
- **settings.gradle**
```
dependencies {
	classpath group: "com.liferay", name: "com.liferay.gradle.plugins.workspace", version: "3.4.23"
	classpath group: "net.saliman", name: "gradle-properties-plugin", version: "1.4.6"
}
```

## Deploy to Liferay
` $ ./gradlew deploy -Pauto.deploy.dir="/path/to/liferay/deploy"`

# Usage

1. Create a new form or editing a existing one.
2. In the *Elements* tab, go to *Customized Elements* and drag the **Account Data Field** and drop it into your form.
3. Configure, inside the *Basic* tab, the **Account Data Field** selecting the attribute that you want to map into the field.
4. **Save your form** to see the changes.

Available Account fields: Account Name, Account Id

**PRs, issues and comments will be welcomed**

<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/martin-dominguez/liferay-modules.svg
[contributors-url]: https://github.com/martin-dominguez/liferay-modules/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/martin-dominguez/liferay-modules.svg
[forks-url]: https://github.com/martin-dominguez/liferay-modules/network/members
[stars-shield]: https://img.shields.io/github/stars/martin-dominguez/liferay-modules.svg
[stars-url]: https://github.com/martin-dominguez/liferay-modules/stargazers
[issues-shield]: https://img.shields.io/github/issues/martin-dominguez/liferay-modules.svg
[issues-url]: https://github.com/martin-dominguez/liferay-modules/issues
[top-lenguage-shield]: https://img.shields.io/github/languages/top/martin-dominguez/liferay-modules
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/-martin-dominguez/
[config-img]: doc-images/user-data-field1.png
[page-img]: doc-images/user-data-field2.png
