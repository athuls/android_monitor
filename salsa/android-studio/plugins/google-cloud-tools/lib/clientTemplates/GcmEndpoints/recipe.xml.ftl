<?xml version="1.0"?>
<recipe>
    <merge from="root/AndroidManifest.xml.ftl" to="${escapeXmlAttribute(manifestOut)}/AndroidManifest.xml" />
    <merge from="root/build.gradle.ftl" to="${escapeXmlAttribute(projectOut)}/build.gradle" />
</recipe>
