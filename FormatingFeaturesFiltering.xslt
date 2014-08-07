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
	<!-- remove all style attributes except those for a table and its cells and to allow underline -->	
	<xsl:template match="*[not(local-name() = &apos;table&apos; or local-name() = &apos;tr&apos; or local-name() = &apos;th&apos; or local-name() = &apos;td&apos; or local-name() = &apos;col&apos;)]/@style"></xsl:template>
	<xsl:template match="span/@style">
		<xsl:if test="contains(.,&apos;text-decoration&apos;)">
		 	<xsl:copy></xsl:copy>
		</xsl:if>
	</xsl:template>
	<!-- remove rowspan and collspan when set to 1 -->
	<xsl:template match="@colspan[.=&apos;1&apos;] | @rowspan[.=&apos;1&apos;]"></xsl:template>
	<!-- remove well known Microsoft Word classes -->
	<xsl:template match="@class[starts-with(., &apos;Mso&apos;)] | @class[starts-with(., &apos;mso&apos;)]"></xsl:template>
	<!-- remove conditional comments that Microsoft Word uses -->
	<xsl:template match="comment()[contains(., &apos;if&apos;)] | comment()[contains(., &apos;endif&apos;)]"></xsl:template>
</xsl:stylesheet>