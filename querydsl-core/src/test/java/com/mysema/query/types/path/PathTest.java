/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.types.path;

import static com.mysema.query.alias.Alias.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Test;

import com.mysema.query.alias.Alias;
import com.mysema.query.annotations.QueryEntity;
import com.mysema.query.annotations.QueryTransient;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathImpl;
import com.mysema.util.AnnotatedElementAdapter;

public class PathTest {

    enum ExampleEnum {A,B}
    
    public static class Superclass {

        @Nullable
        public String getProperty4(){
            return null;
        }
    }

    @QueryEntity
    public static class Entity extends Superclass{

        @Nullable
        private String property1;

        private String property2;

        @QueryTransient
        private String property3;

        public String getProperty1(){
            return property1;
        }

        @Nonnull
        public String getProperty2(){
            return property2;
        }

        @Nonnull
        public String getProperty3(){
            return property3;
        }

    }
    
    @Test
    public void GetAnnotatedElement(){
        Entity entity = Alias.alias(Entity.class);
        AnnotatedElement element = $(entity).getAnnotatedElement();

        // type
        assertEquals(Entity.class, element);
    }

    @Test
    public void GetAnnotatedElement_for_property(){
        Entity entity = Alias.alias(Entity.class);
        AnnotatedElement property1 = $(entity.getProperty1()).getAnnotatedElement();
        AnnotatedElement property2 = $(entity.getProperty2()).getAnnotatedElement();
        AnnotatedElement property3 = $(entity.getProperty3()).getAnnotatedElement();
        AnnotatedElement property4 = $(entity.getProperty4()).getAnnotatedElement();

        // property (field)
        assertEquals(Field.class, property1.getClass());
        assertTrue(property1.isAnnotationPresent(Nullable.class));
        assertNotNull(property1.getAnnotation(Nullable.class));
        assertFalse(property1.isAnnotationPresent(Nonnull.class));
        assertNull(property1.getAnnotation(Nonnull.class));

        // property2 (method)
        assertEquals(Method.class, property2.getClass());
        assertTrue(property2.isAnnotationPresent(Nonnull.class));
        assertNotNull(property2.getAnnotation(Nonnull.class));
        assertFalse(property2.isAnnotationPresent(Nullable.class));
        assertNull(property2.getAnnotation(Nullable.class));

        // property3 (both)
        assertEquals(AnnotatedElementAdapter.class, property3.getClass());
        assertTrue(property3.isAnnotationPresent(QueryTransient.class));
        assertNotNull(property3.getAnnotation(QueryTransient.class));
        assertTrue(property3.isAnnotationPresent(Nonnull.class));
        assertNotNull(property3.getAnnotation(Nonnull.class));
        assertFalse(property3.isAnnotationPresent(Nullable.class));
        assertNull(property3.getAnnotation(Nullable.class));

        // property 4 (superclass)
        assertEquals(Method.class, property4.getClass());
        assertTrue(property4.isAnnotationPresent(Nullable.class));
        assertNotNull(property4.getAnnotation(Nullable.class));

    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void Equals(){
        assertEquals(new StringPath("s"),  new StringPath("s"));
        assertEquals(new BooleanPath("b"), new BooleanPath("b"));
        assertEquals(new NumberPath<Integer>(Integer.class,"n"), new NumberPath<Integer>(Integer.class,"n"));
        
        assertEquals(new ArrayPath(String[].class, "p"), new PathImpl(String.class, "p"));
        assertEquals(new BooleanPath("p"), new PathImpl(Boolean.class, "p"));
        assertEquals(new ComparablePath(String.class,"p"), new PathImpl(String.class, "p"));
        assertEquals(new DatePath(Date.class,"p"), new PathImpl(Date.class, "p"));
        assertEquals(new DateTimePath(Date.class,"p"), new PathImpl(Date.class, "p"));
        assertEquals(new EnumPath(ExampleEnum.class,"p"), new PathImpl(ExampleEnum.class, "p"));
        assertEquals(new NumberPath(Integer.class,"p"), new PathImpl(Integer.class, "p"));
        assertEquals(new StringPath("p"), new PathImpl(String.class, "p"));
        assertEquals(new TimePath(Time.class,"p"), new PathImpl(Time.class, "p"));        
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void Various(){
        List<Path<?>> paths = new ArrayList<Path<?>>();
        paths.add(new ArrayPath(String[].class, "p"));
        paths.add(new BooleanPath("p"));
        paths.add(new ComparablePath(String.class,"p"));
        paths.add(new DatePath(Date.class,"p"));
        paths.add(new DateTimePath(Date.class,"p"));
        paths.add(new EnumPath(ExampleEnum.class,"p"));
        paths.add(new NumberPath(Integer.class,"p"));
        paths.add(new StringPath("p"));
        paths.add(new TimePath(Time.class,"p"));
        
        for (Path<?> path : paths){
            assertNotNull(path.getMetadata());
            assertNotNull(path.getType());
            assertEquals(path, path.getRoot());            
        }
    }
    
}
