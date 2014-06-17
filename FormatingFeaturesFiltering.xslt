<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output omit-xml-declaration="yes" method="xml" cdata-section-elements="script"></xsl:output>
	<xsl:template match="/ | node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*"></xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="*[      (self::br or self::p or self::div)     and      normalize-space(translate(., &apos; &apos;, &apos;&apos;)) = &apos;&apos;     and      not(@*)     and      not(processing-instruction())     and      not(comment())     and      not(*[not(self::br) or @* or * or node()])     and      not(following::node()[not(         (self::text() or self::br or self::p or self::div)        and         normalize-space(translate(., &apos; &apos;, &apos;&apos;)) = &apos;&apos;        and         not(@*)        and         not(processing-instruction())        and         not(comment())        and         not(*[not(self::br) or @* or * or node()])       )])     ]">
		<!-- ignore all paragraphs and line-breaks at the end that have nothing but (non-breaking) spaces and line breaks -->
	</xsl:template>
	<xsl:template match="br[parent::div and not(preceding-sibling::node()) and not(following-sibling::node())]">
		<!-- Chrome generates <div><br/></div>. Renders differently in different browsers. Replace it with a non-breaking space -->
		<xsl:text> </xsl:text>
	</xsl:template>
	<!-- remove disallowed elements but keep its children -->
	<xsl:template match="font">
		<xsl:apply-templates></xsl:apply-templates>
	</xsl:template>
	<!-- remove disallowed attributes -->
	<xsl:template match="@align | @valign | @border | @cellpadding | @cellspacing"></xsl:template>
	<!-- remove all style attributes except those for a table cell to allow center alignment -->	
	<xsl:template match="*[not(local-name() = &apos;td&apos;)]/@style"></xsl:template>
	<xsl:template match="td/@style">
		<xsl:if test="contains(.,&apos;text-align&apos;)">
			<xsl:attribute name="style">
				<xsl:text>text-align:center;</xsl:text>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	<!-- remove rowspan and collspan when set to 1 -->
	<xsl:template match="@colspan[.=&apos;1&apos;] | @rowspan[.=&apos;1&apos;]"></xsl:template>
	<!-- remove well known Microsoft Word classes -->
	<xsl:template match="@class[.=&apos;MsoNormal&apos;] | @class[.=&apos;MsoTableGrid&apos;]"></xsl:template>	
</xsl:stylesheet>