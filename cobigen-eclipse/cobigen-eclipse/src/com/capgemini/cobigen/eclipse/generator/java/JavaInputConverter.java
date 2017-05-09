package com.capgemini.cobigen.eclipse.generator.java;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorCreationException;
import com.capgemini.cobigen.eclipse.common.tools.ClassLoaderUtil;
import com.capgemini.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;
import com.google.common.collect.Lists;

/**
 * Converter to convert the IDE representation of IDE elements to valid input types for the {@link CobiGen
 * generator}
 */
public class JavaInputConverter {

    /**
     * Converts a list of IDE objects to the supported CobiGen input types
     * @param javaElements
     *            java IDE objects (mainly of type {@link IJavaElement}), which should be converted
     * @return the corresponding {@link List} of inputs for the {@link CobiGen generator}
     * @throws GeneratorCreationException
     *             if any exception occurred during converting the inputs or creating the generator
     */
    public static List<Object> convertInput(List<Object> javaElements) throws GeneratorCreationException {
        List<Object> convertedInputs = Lists.newLinkedList();

        /*
         * Precondition / Assumption: all elements of the list are of the same type
         */
        for (Object elem : javaElements) {
            if (elem instanceof IPackageFragment) {
                try {
                    IPackageFragment frag = (IPackageFragment) elem;
                    PackageFolder packageFolder =
                        new PackageFolder(frag.getResource().getLocationURI(), frag.getElementName());
                    packageFolder.setClassLoader(ClassLoaderUtil.getProjectClassLoader(frag.getJavaProject()));
                    convertedInputs.add(packageFolder);
                } catch (MalformedURLException e) {
                    throw new GeneratorCreationException(
                        "An internal exception occurred while building the project class loader.", e);
                } catch (CoreException e) {
                    throw new GeneratorCreationException("An eclipse internal exception occurred.", e);
                }
            } else if (elem instanceof ICompilationUnit) {
                // Take first input type as precondition for the input is that all input types are part of the
                // same project
                try {
                    IType rootType = ((ICompilationUnit) elem).getTypes()[0];
                    try {
                        ClassLoader projectClassLoader =
                            ClassLoaderUtil.getProjectClassLoader(rootType.getJavaProject());
                        Class<?> loadedClass = projectClassLoader.loadClass(rootType.getFullyQualifiedName());
                        Object[] inputSourceAndClass =
                            new Object[] { loadedClass, JavaParserUtil.getFirstJavaClass(projectClassLoader,
                                new StringReader(((ICompilationUnit) elem).getSource())) };
                        convertedInputs.add(inputSourceAndClass);
                    } catch (MalformedURLException e) {
                        throw new GeneratorCreationException("An internal exception occurred while loading Java class "
                            + rootType.getFullyQualifiedName(), e);
                    } catch (ClassNotFoundException e) {
                        throw new GeneratorCreationException(
                            "Could not instantiate Java class " + rootType.getFullyQualifiedName(), e);
                    }
                } catch (MergeException e) {
                    throw new GeneratorCreationException("Could not parse Java base file: "
                        + ((ICompilationUnit) elem).getElementName() + ":\n" + e.getMessage(), e);
                } catch (CoreException e) {
                    throw new GeneratorCreationException("An eclipse internal exception occurred.", e);
                }
            }
        }

        return convertedInputs;
    }
}