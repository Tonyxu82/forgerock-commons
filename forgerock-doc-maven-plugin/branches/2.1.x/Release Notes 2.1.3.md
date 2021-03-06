# ForgeRock Documentation Tools 2.1.3 Release Notes

ForgeRock Documentation Tools is a catch all for the doc build artifacts,
sites where we post release documentation,
and the documentation about documentation.

The link to the online issue tracker is
<https://bugster.forgerock.org/jira/browse/DOCS>.

This release brings the following changes,
and has the following known issues.

## Compatibility

This maintenance release does not introduce configuration changes since 2.1.2.

You need only to update the plugin version.


## Improvements & New Features

**DOCS-72: Improve widow and orphan control in PDF**

You can now use the processing instruction `<?hard-pagebreak?>`
to force an unconditional page break in the PDF output.

This processing instruction cannot be used inline,
but instead must be used between block elements.


## Bugs Fixed

**DOCS-162: `<replaceable>` tags within `<screen>` tags have no effect in the HTML**

**DOCS-173: Link text too dark in top-right banner showing latest release**


## Known Issues

**DOCS-132: Soft hyphens used to break lines are rendered in PDF as hyphen + space**

Although soft hyphens are not used in this release,
the line break for hyphenation still remains.

See <https://issues.apache.org/jira/browse/FOP-2358>.

Workaround: Fix the content after copy/paste.

**DOCS-150: When a code sample is unwrapped, it it not limited to the width of the page**

**DOCS-163: The performance="optional" attr in a step has no effect**


* * *

This work is licensed under the Creative Commons
Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit
<http://creativecommons.org/licenses/by-nc-nd/3.0/>
or send a letter to Creative Commons, 444 Castro Street,
Suite 900, Mountain View, California, 94041, USA.

Copyright 2014 ForgeRock AS
