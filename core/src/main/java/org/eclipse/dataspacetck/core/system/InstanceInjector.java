/*
 *  Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 *
 */

package org.eclipse.dataspacetck.core.system;

import org.eclipse.dataspacetck.core.api.system.ConfigParam;
import org.eclipse.dataspacetck.core.api.system.Inject;
import org.eclipse.dataspacetck.core.spi.boot.Monitor;
import org.eclipse.dataspacetck.core.spi.system.ServiceConfiguration;
import org.eclipse.dataspacetck.core.spi.system.ServiceResolver;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static org.eclipse.dataspacetck.core.system.ConfigFunctions.propertyOrEnv;

/**
 * Injects fields on an instance annotated with {@link Inject} in a type hierarchy.
 */
public class InstanceInjector {
    private ServiceResolver resolver;
    private ExtensionContext extensionContext;
    private Monitor monitor;

    public InstanceInjector(ServiceResolver resolver, ExtensionContext extensionContext, Monitor monitor) {
        this.resolver = requireNonNull(resolver);
        this.extensionContext = requireNonNull(extensionContext);
        this.monitor = requireNonNull(monitor);
    }

    public void inject(Object instance) {
        visitSuperClasses(instance.getClass(), instance);
        visitFields(instance.getClass(), instance);
    }

    private void visit(Class<?> clazz, Object instance) {
        visitSuperClasses(clazz, instance);
        visitFields(clazz, instance);
    }

    private void visitSuperClasses(Class<?> clazz, Object instance) {
        var superClass = clazz.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {
            visit(superClass, instance);
        }
    }

    private void visitFields(Class<?> clazz, Object instance) {
        for (var field : clazz.getDeclaredFields()) {
            if (!visitInjectField(field, instance)) {
                visitConfigField(field, instance);
            }
        }
    }

    private void visitConfigField(Field field, Object instance) {
        var annotations = field.getDeclaredAnnotations();
        var annotation = stream(annotations).filter(a -> a.annotationType().equals(ConfigParam.class)).findFirst();
        if (annotation.isEmpty()) {
            return;
        }
        String key = getKey(field);
        var value = extensionContext.getConfigurationParameter(key).orElse(propertyOrEnv(key, null));
        if (value == null) {
            if (((ConfigParam) annotation.get()).required()) {
                var className = field.getDeclaringClass().getName();
                var fieldName = field.getName();
                throw new RuntimeException(format("Required configuration '%s' not found [%s.%s]. Please set the environment variable.", key, className, fieldName));
            }
            return;
        }
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(format("Error setting field configuration value '%s': %s", field.getName(), field.getType()), e);
        }
    }

    private boolean visitInjectField(Field field, Object instance) {
        var annotations = field.getDeclaredAnnotations();
        if (stream(annotations).noneMatch(a -> a.annotationType().equals(Inject.class))) {
            return false;
        }
        var tags = extensionContext.getTags();
        var id = extensionContext.getUniqueId();
        var configuration = ServiceConfiguration.Builder.newInstance()
                .tags(tags)
                .scopeId(id)
                .annotations(annotations)
                .monitor(monitor)
                .propertyDelegate(k -> extensionContext.getConfigurationParameter(k).orElse(propertyOrEnv(k, null)))
                .build();

        try {
            field.setAccessible(true);
            field.set(instance, resolver.resolve(field.getType(), configuration));
            return true;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(format("Error injecting field %s with: %s", field.getName(), field.getType()), e);
        }
    }

    @NotNull
    private String getKey(Field field) {
        var keyPrefix = extensionContext.getRequiredTestMethod().getName().toUpperCase();
        var keyPostfix = field.getName().toUpperCase();
        return keyPrefix + "_" + keyPostfix;
    }
}
