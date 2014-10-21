<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:java="http://xml.apache.org/xalan/java"
                version="1.0"
                exclude-result-prefixes="java">
    <xsl:output omit-xml-declaration="yes"/>

    <xsl:param name="contextpath"/>

    <xsl:template match="xhtml:img" priority="1">
        <xsl:element name="{local-name(.)}">
            <xsl:for-each select="@*">
                <xsl:choose>
                    <xsl:when test="not($contextpath='') and local-name(.)='src'">
                        <xsl:attribute name="{local-name(.)}">
                            <xsl:value-of select="concat($contextpath,.)"/>
                        </xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="{local-name(.)}">
                            <xsl:value-of select="."/>
                        </xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
            <xsl:apply-templates select="node()"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="*[@xlink:href]">
        <xsl:variable name="factory"
                      select="java:org.dd4t.core.factories.impl.LinkResolverFactory.getInstance()"/>
        <xsl:variable name="resolver"
                      select="java:getLinkResolver($factory)"/>
        <xsl:variable name="resolved-link"
                      select="java:resolve($resolver, string(@xlink:href))"/>
        <xsl:choose>
            <xsl:when test="string($resolved-link)=''">
                <xsl:apply-templates select="node()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="{local-name(.)}">
                    <xsl:attribute name="href">
                        <xsl:value-of select="$contextpath"/><xsl:value-of select="$resolved-link"/>
                    </xsl:attribute>
                    <xsl:for-each select="@*">
                        <xsl:if test="not(local-name(.)='href')">
                            <xsl:attribute name="{local-name(.)}">
                                <xsl:value-of select="."/>
                            </xsl:attribute>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:apply-templates select="node()"/>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
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

    <xsl:template match="*">
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