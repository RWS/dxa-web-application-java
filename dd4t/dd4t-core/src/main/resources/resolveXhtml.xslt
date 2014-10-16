<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                version="1.0">
    <xsl:output omit-xml-declaration="yes"/>

    <xsl:template match="@xlink:*">
        <xsl:attribute name="{local-name(.)}">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="@xhtml:*">
        <xsl:attribute name="{local-name(.)}">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="xhtml:*">
        <xsl:element name="{local-name(.)}">
            <xsl:for-each select="@*">
                <xsl:attribute name="{local-name(.)}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:for-each>
            <xsl:apply-templates select="node()"/>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>