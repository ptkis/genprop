'Generate toString()' a plugin for Intellij IDEA
================================================

Author:
    Claus Ibsen, cib_rejse@yahoo.dk (private) or csi@maerskdata.dk (work)

Official home site:
    http://www.intellij.org/twiki/bin/view/Main/ToStringPlugin

The todo list is what I have planned to do in the near future.
Suggested features/ideas are just some toughts that could be added in the future.
If you need a feature feel free to drop me an email even though it is listed here
I will prioritize suggestions from the community.

Todo/Planned:
=============
- 

Suggestions/Ideas:
==================
- MethodElement to have more information: hasParameters, throwsException and much more.
- Improve documentation (images in wiki, more velocity examples using $class features)
- Any JDK1.5 impacts
- Calculate average length of toString length to initialize StringBuffer initial capacity (should only be possible for fixed length toString's)
- Refactor GenerateToStringAction seperating actual code generation logic from IDEA and UI
- Always use template from file instead of IDEA streamed configuration
- Generate toString() for multiple classes (for fast generation of toString() in classes that for instance has been code generated)
- List of recently used templates (like ctrl + e) to choose template to apply
- Code style, using [ instead of {) and so on
- Settings: List of excluded fields user can enter classnames, not requiring to know reg.exp.
- Default tempalte for stringbuffer should output strings enclosed with '
- Regular exp. syntax checker
- Insert public String toString() into templates to prepare for generating other methods in the future
- Possible to generate other methods than just 'toString'
- Dump protected/packagelocal fields from super instead of super.toString()
- Option to: Jump in editor to toString() method
- Settings where user can input list of excluded class pattern names for DoesNotOverwriteToString (eg .*Exception)
- Settings where user can input list of included class pattern names for DoesNotOverwriteToString (eg .*model.*)
- Inspection using Psi code to identifty if a field is dumped or not
- Inspection to be able to handle getMethods also (MethodNotUsedInToStringInspection)
