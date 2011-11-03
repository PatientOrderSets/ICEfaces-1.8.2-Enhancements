                     COMPONENT SHOWCASE GETTING STARTED GUIDE
                           
                         (www.icefaces.org)
--------------------------------------------------------------------------------

TOPICS (DEVELOPER NOTES)

1. What is Component Showcase?
2. Project Layout
3. Building the Project

--------------------------------------------------------------------------------
1. What is Component Showcase?

    The component showcase is a collection of small examples intended to aid 
users in the evaluation of ICEfaces features and provide developers with simple
straight forward examples of component usage.  

The component showcase is a complete application that comes in many different
varieties; JSP, Facelets, Portlets and Facelets Enhanced.  Each variety show how
ICEfaces component can be configured to run in the respective environment.

   JSP
    Default implementation using Sun JSF RI or MyFaces.  Simple navigation 
    system which has a link for every component example.  The main layout
    construct is a panel stack which is manipulated by the navigation system.
    Requires JDK 1.4 or higher to run.

   Facelets
    Similar to JSP version but uses dynamic includes and Facelets templating
    for the application.  Requires JDK 1.4 or higher to run.

   Portlets
    Each component example is configured to be a porlet.  Individual portlets
    can then be added to the Portal of choice.  Requires JDK 1.4 or higher to
    run.

   Facelets Enhanced
    Facelets based example which uses meta-data to build the UI content.  This
    example Uses JAXB 2.1 and requires JDK 1.5 to compile and run.  Requires 
    JDK 1.5 or higher to run.


--------------------------------------------------------------------------------
2. Project Layout
    
    svn/repo/applications/component-showcase/trunk/
       
       ./common-src/   # common Java source code (JDK 1.4+)
       ./common-web/   # common JSP code 
       ./facelets-enh/ # enhanced public demo (JDK 1.5+)
       ./facelets/     # plain/simple facelets showcase (JDK 1.4+)
       ./jsp/          # plain/simple regular jsp showcase (JDK 1.4+)
       ./portlet/      # plain/simple portlet showcase (JDK 1.4+)
  
    Each version of showcase has is own build file which extend the
    build-common.xml build file.  The build-common.xml base build file is used
    by all ICEfaces sample application.  All common source code is copied by the
    build scripts into the respective project being built.  
       
--------------------------------------------------------------------------------
3. Building the Project

   The component showcase currently has four implementations; JSP, Facelets,
   Portlet and Facelets Enhance.  Each implementation has a build file with
   multiple targets for different Servlet Containers and environements.  Use
   the command "ant help" to see the available options.

   Facelets Enhanced requires that the JAXB libraries 2.0 or higher.  The lib
   folder in the dir component-showcase contains JAXB 2.1 jars and they are
   included in the build by default.  If you are using JDK 1.6 it is suggested
   that you used the ant property -DisJDK16 to exclude the JAXB libraries from
   the component-showcase.war

