<?xml version="1.0" encoding="UTF-8"?>
<!--
///   
/// Copyright 2011 Capgemini & SDL
///
///   Licensed under the Apache License, Version 2.0 (the "License");
///   you may not use this file except in compliance with the License.
///   You may obtain a copy of the License at
///
///       http://www.apache.org/licenses/LICENSE-2.0
///
///   Unless required by applicable law or agreed to in writing, software
///   distributed under the License is distributed on an "AS IS" BASIS,
///   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
///   See the License for the specific language governing permissions and
///   limitations under the License.
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:java="http://xml.apache.org/xalan/java"
    exclude-result-prefixes="java"
    >
    <xsl:output omit-xml-declaration="yes"/>
    <xsl:param name="contextpath"/>
	<xsl:template match="xhtml:img" priority="1">
		<xsl:element name="{local-name(.)}">
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not($contextpath='') and local-name(.)='src'">
						<xsl:attribute name="{local-name(.)}"><xsl:value-of select="concat($contextpath,.)"/></xsl:attribute>					
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="{local-name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose> 
			</xsl:for-each>
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="*[@xlink:href]">
	    <xsl:variable name="linkresolver"       
	        select="java:org.dd4t.core.resolvers.impl.DefaultLinkResolver.new()"/>	
	    <xsl:variable name="resolved-link" 
	        select="java:resolve($linkresolver, string(@xlink:href))"/>
	    <xsl:choose>
	    	<xsl:when test="string($resolved-link)=''">
				<xsl:apply-templates select="node()"/>	    	
	    	</xsl:when>
	    	<xsl:otherwise>
		<xsl:element name="{local-name(.)}">
					<xsl:attribute name="href"><xsl:value-of select="$contextpath"/><xsl:value-of select="$resolved-link"/></xsl:attribute>
					<xsl:for-each select="@*">
						<xsl:if test="not(local-name(.)='href')">
							<xsl:attribute name="{local-name(.)}"><xsl:value-of select="."/></xsl:attribute>
						</xsl:if>
					</xsl:for-each>
					<xsl:apply-templates select="node()"/>
		</xsl:element>
	    	</xsl:otherwise>
	    </xsl:choose>		
	</xsl:template>
	<xsl:template match="@xhtml:*">
		<xsl:attribute name="{local-name(.)}"><xsl:value-of select="."/></xsl:attribute>
	</xsl:template>
	<xsl:template match="xhtml:*">
		<xsl:element name="{local-name(.)}">
			<xsl:for-each select="@*">
				<xsl:attribute name="{local-name(.)}"><xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="*">
		<xsl:element name="{local-name(.)}">
			<xsl:for-each select="@*">
				<xsl:attribute name="{local-name(.)}"><xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>