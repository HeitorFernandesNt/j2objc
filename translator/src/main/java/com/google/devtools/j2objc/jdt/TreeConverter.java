/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devtools.j2objc.jdt;

import com.google.common.collect.Lists;
import com.google.devtools.j2objc.Options;
import com.google.devtools.j2objc.ast.AbstractTypeDeclaration;
import com.google.devtools.j2objc.ast.AnnotatableType;
import com.google.devtools.j2objc.ast.Annotation;
import com.google.devtools.j2objc.ast.AnnotationTypeDeclaration;
import com.google.devtools.j2objc.ast.AnnotationTypeMemberDeclaration;
import com.google.devtools.j2objc.ast.AnonymousClassDeclaration;
import com.google.devtools.j2objc.ast.ArrayAccess;
import com.google.devtools.j2objc.ast.ArrayCreation;
import com.google.devtools.j2objc.ast.ArrayInitializer;
import com.google.devtools.j2objc.ast.ArrayType;
import com.google.devtools.j2objc.ast.AssertStatement;
import com.google.devtools.j2objc.ast.Assignment;
import com.google.devtools.j2objc.ast.Block;
import com.google.devtools.j2objc.ast.BlockComment;
import com.google.devtools.j2objc.ast.BodyDeclaration;
import com.google.devtools.j2objc.ast.BooleanLiteral;
import com.google.devtools.j2objc.ast.BreakStatement;
import com.google.devtools.j2objc.ast.CastExpression;
import com.google.devtools.j2objc.ast.CatchClause;
import com.google.devtools.j2objc.ast.CharacterLiteral;
import com.google.devtools.j2objc.ast.ClassInstanceCreation;
import com.google.devtools.j2objc.ast.CompilationUnit;
import com.google.devtools.j2objc.ast.ConditionalExpression;
import com.google.devtools.j2objc.ast.ConstructorInvocation;
import com.google.devtools.j2objc.ast.ContinueStatement;
import com.google.devtools.j2objc.ast.CreationReference;
import com.google.devtools.j2objc.ast.Dimension;
import com.google.devtools.j2objc.ast.DoStatement;
import com.google.devtools.j2objc.ast.EmptyStatement;
import com.google.devtools.j2objc.ast.EnhancedForStatement;
import com.google.devtools.j2objc.ast.EnumConstantDeclaration;
import com.google.devtools.j2objc.ast.EnumDeclaration;
import com.google.devtools.j2objc.ast.Expression;
import com.google.devtools.j2objc.ast.ExpressionMethodReference;
import com.google.devtools.j2objc.ast.ExpressionStatement;
import com.google.devtools.j2objc.ast.FieldAccess;
import com.google.devtools.j2objc.ast.FieldDeclaration;
import com.google.devtools.j2objc.ast.ForStatement;
import com.google.devtools.j2objc.ast.IfStatement;
import com.google.devtools.j2objc.ast.InfixExpression;
import com.google.devtools.j2objc.ast.InfixExpression.Operator;
import com.google.devtools.j2objc.ast.Initializer;
import com.google.devtools.j2objc.ast.InstanceofExpression;
import com.google.devtools.j2objc.ast.IntersectionType;
import com.google.devtools.j2objc.ast.Javadoc;
import com.google.devtools.j2objc.ast.LabeledStatement;
import com.google.devtools.j2objc.ast.LambdaExpression;
import com.google.devtools.j2objc.ast.LineComment;
import com.google.devtools.j2objc.ast.MarkerAnnotation;
import com.google.devtools.j2objc.ast.MemberValuePair;
import com.google.devtools.j2objc.ast.MethodDeclaration;
import com.google.devtools.j2objc.ast.MethodInvocation;
import com.google.devtools.j2objc.ast.Name;
import com.google.devtools.j2objc.ast.NameQualifiedType;
import com.google.devtools.j2objc.ast.NormalAnnotation;
import com.google.devtools.j2objc.ast.NullLiteral;
import com.google.devtools.j2objc.ast.NumberLiteral;
import com.google.devtools.j2objc.ast.PackageDeclaration;
import com.google.devtools.j2objc.ast.ParameterizedType;
import com.google.devtools.j2objc.ast.ParenthesizedExpression;
import com.google.devtools.j2objc.ast.PostfixExpression;
import com.google.devtools.j2objc.ast.PrefixExpression;
import com.google.devtools.j2objc.ast.PrimitiveType;
import com.google.devtools.j2objc.ast.PropertyAnnotation;
import com.google.devtools.j2objc.ast.QualifiedName;
import com.google.devtools.j2objc.ast.QualifiedType;
import com.google.devtools.j2objc.ast.ReturnStatement;
import com.google.devtools.j2objc.ast.SimpleName;
import com.google.devtools.j2objc.ast.SimpleType;
import com.google.devtools.j2objc.ast.SingleMemberAnnotation;
import com.google.devtools.j2objc.ast.SingleVariableDeclaration;
import com.google.devtools.j2objc.ast.SourcePosition;
import com.google.devtools.j2objc.ast.Statement;
import com.google.devtools.j2objc.ast.StringLiteral;
import com.google.devtools.j2objc.ast.SuperConstructorInvocation;
import com.google.devtools.j2objc.ast.SuperFieldAccess;
import com.google.devtools.j2objc.ast.SuperMethodInvocation;
import com.google.devtools.j2objc.ast.SuperMethodReference;
import com.google.devtools.j2objc.ast.SwitchCase;
import com.google.devtools.j2objc.ast.SwitchStatement;
import com.google.devtools.j2objc.ast.SynchronizedStatement;
import com.google.devtools.j2objc.ast.TagElement;
import com.google.devtools.j2objc.ast.TextElement;
import com.google.devtools.j2objc.ast.ThisExpression;
import com.google.devtools.j2objc.ast.ThrowStatement;
import com.google.devtools.j2objc.ast.TreeNode;
import com.google.devtools.j2objc.ast.TryStatement;
import com.google.devtools.j2objc.ast.Type;
import com.google.devtools.j2objc.ast.TypeDeclaration;
import com.google.devtools.j2objc.ast.TypeDeclarationStatement;
import com.google.devtools.j2objc.ast.TypeLiteral;
import com.google.devtools.j2objc.ast.TypeMethodReference;
import com.google.devtools.j2objc.ast.UnionType;
import com.google.devtools.j2objc.ast.VariableDeclarationExpression;
import com.google.devtools.j2objc.ast.VariableDeclarationFragment;
import com.google.devtools.j2objc.ast.VariableDeclarationStatement;
import com.google.devtools.j2objc.ast.WhileStatement;
import com.google.devtools.j2objc.util.BindingUtil;
import com.google.devtools.j2objc.util.ParserEnvironment;
import com.google.j2objc.annotations.Property;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

/**
 * Converts a Java AST from the JDT data structure to our J2ObjC data structure.
 */
public class TreeConverter {

  public static CompilationUnit convertCompilationUnit(
      ParserEnvironment env, org.eclipse.jdt.core.dom.CompilationUnit jdtUnit,
      String sourceFilePath, String mainTypeName, String source) {
    return new CompilationUnit(env, jdtUnit, sourceFilePath, mainTypeName, source);
  }

  public static Statement convertStatement(org.eclipse.jdt.core.dom.Statement jdtStatement) {
    return (Statement) convert(jdtStatement);
  }

  public static TreeNode convert(Object obj) {
    if (obj == null) {
      return null;
    }
    ASTNode jdtNode = (ASTNode) obj;
    TreeNode node = convertInner(jdtNode)
        .setPosition(getPosition(jdtNode));
    node.validate();
    return node;
  }

  private static SourcePosition getPosition(ASTNode jdtNode) {
    int startPosition = jdtNode.getStartPosition();
    int length = jdtNode.getLength();
    ASTNode root = jdtNode.getRoot();
    if (root instanceof org.eclipse.jdt.core.dom.CompilationUnit) {
      int line = ((org.eclipse.jdt.core.dom.CompilationUnit) root).getLineNumber(startPosition);
      return new SourcePosition(startPosition, length, line);
    } else {
      return new SourcePosition(startPosition, length);
    }
  }

  private static TreeNode convertInner(ASTNode jdtNode) {
    switch (jdtNode.getNodeType()) {
      case ASTNode.ANNOTATION_TYPE_DECLARATION:
        return convertAnnotationTypeDeclaration(
            (org.eclipse.jdt.core.dom.AnnotationTypeDeclaration) jdtNode);
      case ASTNode.ANNOTATION_TYPE_MEMBER_DECLARATION:
        return convertAnnotationTypeMemberDeclaration(
            (org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration) jdtNode);
      case ASTNode.ANONYMOUS_CLASS_DECLARATION:
        return convertAnonymousClassDeclaration(
            (org.eclipse.jdt.core.dom.AnonymousClassDeclaration) jdtNode);
      case ASTNode.ARRAY_ACCESS:
        return convertArrayAccess((org.eclipse.jdt.core.dom.ArrayAccess) jdtNode);
      case ASTNode.ARRAY_CREATION:
        return convertArrayCreation((org.eclipse.jdt.core.dom.ArrayCreation) jdtNode);
      case ASTNode.ARRAY_INITIALIZER:
        return convertArrayInitializer((org.eclipse.jdt.core.dom.ArrayInitializer) jdtNode);
      case ASTNode.ARRAY_TYPE:
        return convertArrayType((org.eclipse.jdt.core.dom.ArrayType) jdtNode);
      case ASTNode.ASSERT_STATEMENT:
        return convertAssertStatement((org.eclipse.jdt.core.dom.AssertStatement) jdtNode);
      case ASTNode.ASSIGNMENT:
        return convertAssignment((org.eclipse.jdt.core.dom.Assignment) jdtNode);
      case ASTNode.BLOCK:
        return convertBlock((org.eclipse.jdt.core.dom.Block) jdtNode);
      case ASTNode.BLOCK_COMMENT:
        return new BlockComment();
      case ASTNode.BOOLEAN_LITERAL:
        return convertBooleanLiteral((org.eclipse.jdt.core.dom.BooleanLiteral) jdtNode);
      case ASTNode.BREAK_STATEMENT:
        return convertBreakStatement((org.eclipse.jdt.core.dom.BreakStatement) jdtNode);
      case ASTNode.CAST_EXPRESSION:
        return convertCastExpression((org.eclipse.jdt.core.dom.CastExpression) jdtNode);
      case ASTNode.CATCH_CLAUSE:
        return convertCatchClause((org.eclipse.jdt.core.dom.CatchClause) jdtNode);
      case ASTNode.CHARACTER_LITERAL:
        return convertCharacterLiteral((org.eclipse.jdt.core.dom.CharacterLiteral) jdtNode);
      case ASTNode.CLASS_INSTANCE_CREATION:
        return new ClassInstanceCreation((org.eclipse.jdt.core.dom.ClassInstanceCreation) jdtNode);
      case ASTNode.CONDITIONAL_EXPRESSION:
        return convertConditionalExpression(
            (org.eclipse.jdt.core.dom.ConditionalExpression) jdtNode);
      case ASTNode.CONSTRUCTOR_INVOCATION:
        return convertConstructorInvocation(
            (org.eclipse.jdt.core.dom.ConstructorInvocation) jdtNode);
      case ASTNode.CONTINUE_STATEMENT:
        return convertContinueStatement((org.eclipse.jdt.core.dom.ContinueStatement) jdtNode);
      case ASTNode.CREATION_REFERENCE:
        return new CreationReference((org.eclipse.jdt.core.dom.CreationReference) jdtNode);
      case ASTNode.DIMENSION:
        return convertDimension((org.eclipse.jdt.core.dom.Dimension) jdtNode);
      case ASTNode.DO_STATEMENT:
        return convertDoStatement((org.eclipse.jdt.core.dom.DoStatement) jdtNode);
      case ASTNode.EMPTY_STATEMENT:
        return new EmptyStatement();
      case ASTNode.ENHANCED_FOR_STATEMENT:
        return convertEnhancedForStatement((org.eclipse.jdt.core.dom.EnhancedForStatement) jdtNode);
      case ASTNode.ENUM_CONSTANT_DECLARATION:
        return convertEnumConstantDeclaration(
            (org.eclipse.jdt.core.dom.EnumConstantDeclaration) jdtNode);
      case ASTNode.ENUM_DECLARATION:
        return convertEnumDeclaration((org.eclipse.jdt.core.dom.EnumDeclaration) jdtNode);
      case ASTNode.EXPRESSION_METHOD_REFERENCE:
        return new ExpressionMethodReference(
            (org.eclipse.jdt.core.dom.ExpressionMethodReference) jdtNode);
      case ASTNode.EXPRESSION_STATEMENT:
        return convertExpressionStatement((org.eclipse.jdt.core.dom.ExpressionStatement) jdtNode);
      case ASTNode.FIELD_ACCESS:
        return convertFieldAccess((org.eclipse.jdt.core.dom.FieldAccess) jdtNode);
      case ASTNode.FIELD_DECLARATION:
        return convertFieldDeclaration((org.eclipse.jdt.core.dom.FieldDeclaration) jdtNode);
      case ASTNode.FOR_STATEMENT:
        return convertForStatement((org.eclipse.jdt.core.dom.ForStatement) jdtNode);
      case ASTNode.IF_STATEMENT:
        return convertIfStatement((org.eclipse.jdt.core.dom.IfStatement) jdtNode);
      case ASTNode.INFIX_EXPRESSION:
        return convertInfixExpression((org.eclipse.jdt.core.dom.InfixExpression) jdtNode);
      case ASTNode.INTERSECTION_TYPE:
        return convertIntersectionType((org.eclipse.jdt.core.dom.IntersectionType) jdtNode);
      case ASTNode.INITIALIZER:
        return convertInitializer((org.eclipse.jdt.core.dom.Initializer) jdtNode);
      case ASTNode.INSTANCEOF_EXPRESSION:
        return convertInstanceofExpression((org.eclipse.jdt.core.dom.InstanceofExpression) jdtNode);
      case ASTNode.JAVADOC:
        return convertJavadoc((org.eclipse.jdt.core.dom.Javadoc) jdtNode);
      case ASTNode.LABELED_STATEMENT:
        return convertLabeledStatement((org.eclipse.jdt.core.dom.LabeledStatement) jdtNode);
      case ASTNode.LAMBDA_EXPRESSION:
        return new LambdaExpression((org.eclipse.jdt.core.dom.LambdaExpression) jdtNode);
      case ASTNode.LINE_COMMENT:
        return new LineComment();
      case ASTNode.MARKER_ANNOTATION:
        return convertMarkerAnnotation((org.eclipse.jdt.core.dom.MarkerAnnotation) jdtNode);
      case ASTNode.MEMBER_VALUE_PAIR:
        return convertMemberValuePair((org.eclipse.jdt.core.dom.MemberValuePair) jdtNode);
      case ASTNode.METHOD_DECLARATION:
        return new MethodDeclaration((org.eclipse.jdt.core.dom.MethodDeclaration) jdtNode);
      case ASTNode.METHOD_INVOCATION:
        return new MethodInvocation((org.eclipse.jdt.core.dom.MethodInvocation) jdtNode);
      case ASTNode.NAME_QUALIFIED_TYPE:
        return convertNameQualifiedType((org.eclipse.jdt.core.dom.NameQualifiedType) jdtNode);
      case ASTNode.NORMAL_ANNOTATION:
        return convertNormalAnnotation((org.eclipse.jdt.core.dom.NormalAnnotation) jdtNode);
      case ASTNode.NULL_LITERAL:
        return new NullLiteral();
      case ASTNode.NUMBER_LITERAL:
        return convertNumberLiteral((org.eclipse.jdt.core.dom.NumberLiteral) jdtNode);
      case ASTNode.PACKAGE_DECLARATION:
        return convertPackageDeclaration((org.eclipse.jdt.core.dom.PackageDeclaration) jdtNode);
      case ASTNode.PARAMETERIZED_TYPE:
        return new ParameterizedType((org.eclipse.jdt.core.dom.ParameterizedType) jdtNode);
      case ASTNode.PARENTHESIZED_EXPRESSION:
        return new ParenthesizedExpression(
            (org.eclipse.jdt.core.dom.ParenthesizedExpression) jdtNode);
      case ASTNode.POSTFIX_EXPRESSION:
        return new PostfixExpression((org.eclipse.jdt.core.dom.PostfixExpression) jdtNode);
      case ASTNode.PREFIX_EXPRESSION:
        return new PrefixExpression((org.eclipse.jdt.core.dom.PrefixExpression) jdtNode);
      case ASTNode.PRIMITIVE_TYPE:
        return convertPrimitiveType((org.eclipse.jdt.core.dom.PrimitiveType) jdtNode);
      case ASTNode.QUALIFIED_NAME:
        return new QualifiedName((org.eclipse.jdt.core.dom.QualifiedName) jdtNode);
      case ASTNode.QUALIFIED_TYPE:
        return convertQualifiedType((org.eclipse.jdt.core.dom.QualifiedType) jdtNode);
      case ASTNode.RETURN_STATEMENT:
        return new ReturnStatement((org.eclipse.jdt.core.dom.ReturnStatement) jdtNode);
      case ASTNode.SIMPLE_NAME:
        return new SimpleName((org.eclipse.jdt.core.dom.SimpleName) jdtNode);
      case ASTNode.SIMPLE_TYPE:
        return convertSimpleType((org.eclipse.jdt.core.dom.SimpleType) jdtNode);
      case ASTNode.SINGLE_MEMBER_ANNOTATION:
        return convertSingleMemberAnnotation(
            (org.eclipse.jdt.core.dom.SingleMemberAnnotation) jdtNode);
      case ASTNode.SINGLE_VARIABLE_DECLARATION:
        return new SingleVariableDeclaration(
            (org.eclipse.jdt.core.dom.SingleVariableDeclaration) jdtNode);
      case ASTNode.STRING_LITERAL:
        return new StringLiteral((org.eclipse.jdt.core.dom.StringLiteral) jdtNode);
      case ASTNode.SUPER_CONSTRUCTOR_INVOCATION:
        return new SuperConstructorInvocation(
            (org.eclipse.jdt.core.dom.SuperConstructorInvocation) jdtNode);
      case ASTNode.SUPER_FIELD_ACCESS:
        return convertSuperFieldAccess((org.eclipse.jdt.core.dom.SuperFieldAccess) jdtNode);
      case ASTNode.SUPER_METHOD_INVOCATION:
        return new SuperMethodInvocation((org.eclipse.jdt.core.dom.SuperMethodInvocation) jdtNode);
      case ASTNode.SUPER_METHOD_REFERENCE:
        return new SuperMethodReference((org.eclipse.jdt.core.dom.SuperMethodReference) jdtNode);
      case ASTNode.SWITCH_CASE:
        return new SwitchCase((org.eclipse.jdt.core.dom.SwitchCase) jdtNode);
      case ASTNode.SWITCH_STATEMENT:
        return new SwitchStatement((org.eclipse.jdt.core.dom.SwitchStatement) jdtNode);
      case ASTNode.SYNCHRONIZED_STATEMENT:
        return new SynchronizedStatement((org.eclipse.jdt.core.dom.SynchronizedStatement) jdtNode);
      case ASTNode.TAG_ELEMENT:
        return new TagElement((org.eclipse.jdt.core.dom.TagElement) jdtNode);
      case ASTNode.TEXT_ELEMENT:
        return new TextElement((org.eclipse.jdt.core.dom.TextElement) jdtNode);
      case ASTNode.THIS_EXPRESSION:
        return new ThisExpression((org.eclipse.jdt.core.dom.ThisExpression) jdtNode);
      case ASTNode.THROW_STATEMENT:
        return new ThrowStatement((org.eclipse.jdt.core.dom.ThrowStatement) jdtNode);
      case ASTNode.TRY_STATEMENT:
        return new TryStatement((org.eclipse.jdt.core.dom.TryStatement) jdtNode);
      case ASTNode.TYPE_DECLARATION:
        return convertTypeDeclaration((org.eclipse.jdt.core.dom.TypeDeclaration) jdtNode);
      case ASTNode.TYPE_DECLARATION_STATEMENT:
        return new TypeDeclarationStatement(
            (org.eclipse.jdt.core.dom.TypeDeclarationStatement) jdtNode);
      case ASTNode.TYPE_LITERAL:
        return new TypeLiteral((org.eclipse.jdt.core.dom.TypeLiteral) jdtNode);
      case ASTNode.TYPE_METHOD_REFERENCE:
        return new TypeMethodReference((org.eclipse.jdt.core.dom.TypeMethodReference) jdtNode);
      case ASTNode.UNION_TYPE:
        return new UnionType((org.eclipse.jdt.core.dom.UnionType) jdtNode);
      case ASTNode.VARIABLE_DECLARATION_EXPRESSION:
        return new VariableDeclarationExpression(
            (org.eclipse.jdt.core.dom.VariableDeclarationExpression) jdtNode);
      case ASTNode.VARIABLE_DECLARATION_FRAGMENT:
        return new VariableDeclarationFragment(
            (org.eclipse.jdt.core.dom.VariableDeclarationFragment) jdtNode);
      case ASTNode.VARIABLE_DECLARATION_STATEMENT:
        return new VariableDeclarationStatement(
            (org.eclipse.jdt.core.dom.VariableDeclarationStatement) jdtNode);
      case ASTNode.WHILE_STATEMENT:
        return convertWhileStatement((org.eclipse.jdt.core.dom.WhileStatement) jdtNode);
      // These nodes only appear in comments and J2ObjC doens't need any
      // information from their subtree so we just convert them to TextElement.
      case ASTNode.MEMBER_REF:
      case ASTNode.METHOD_REF:
      case ASTNode.METHOD_REF_PARAMETER:
        return new TextElement(jdtNode);
      case ASTNode.COMPILATION_UNIT:
        throw new AssertionError(
            "CompilationUnit must be converted using convertCompilationUnit()");
      default:
        throw new AssertionError("Unknown node type: " + jdtNode.getClass().getName());
    }
  }

  private static TreeNode convertAnnotatableType(
      org.eclipse.jdt.core.dom.AnnotatableType node, AnnotatableType newNode) {
    if (Options.isJava8Translator()) {
      for (Object x : node.annotations()) {
        newNode.addAnnotation((Annotation) convert(x));
      }
    }
    return newNode;
  }

  private static TreeNode convertAbstractTypeDeclaration(
      org.eclipse.jdt.core.dom.AbstractTypeDeclaration node, AbstractTypeDeclaration newNode) {
    convertBodyDeclaration(node, newNode);
    List<BodyDeclaration> bodyDeclarations = new ArrayList<>();
    for (Object bodyDecl : node.bodyDeclarations()) {
      bodyDeclarations.add((BodyDeclaration) convert(bodyDecl));
    }
    return newNode
        .setName((SimpleName) convert(node.getName()))
        .setTypeElement(BindingConverter.getTypeElement(node.resolveBinding()))
        .setBodyDeclarations(bodyDeclarations);
  }

  private static TreeNode convertAnnotationTypeDeclaration(
      org.eclipse.jdt.core.dom.AnnotationTypeDeclaration node) {
    return convertAbstractTypeDeclaration(node, new AnnotationTypeDeclaration());
  }

  private static TreeNode convertAnnotationTypeMemberDeclaration(
      org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration node) {
    AnnotationTypeMemberDeclaration newNode = new AnnotationTypeMemberDeclaration();
    convertBodyDeclaration(node, newNode);
    return newNode
        .setElement((ExecutableElement) BindingConverter.getElement(node.resolveBinding()))
        .setName((SimpleName) convert(node.getName()))
        .setType((Type) convert(node.getType()))
        .setDefault((Expression) convert(node.getDefault()));
  }

  private static TreeNode convertAnonymousClassDeclaration(
      org.eclipse.jdt.core.dom.AnonymousClassDeclaration node) {
    List<BodyDeclaration> bodyDeclarations = new ArrayList<>();
    for (Object bodyDecl : node.bodyDeclarations()) {
      bodyDeclarations.add((BodyDeclaration) convert(bodyDecl));
    }
    return new AnonymousClassDeclaration()
        .setElement((TypeElement) BindingConverter.getElement(node.resolveBinding()))
        .setBodyDeclarations(bodyDeclarations);
  }

  private static TreeNode convertArrayAccess(org.eclipse.jdt.core.dom.ArrayAccess node) {
    return new ArrayAccess()
        .setArray((Expression) convert(node.getArray()))
        .setIndex((Expression) convert(node.getIndex()));
  }

  private static TreeNode convertArrayCreation(org.eclipse.jdt.core.dom.ArrayCreation node) {
    ArrayCreation newNode = new ArrayCreation();
    convertExpression(node, newNode);
    List<Expression> dimensions = new ArrayList<>();
    for (Object dimension : node.dimensions()) {
      dimensions.add((Expression) convert(dimension));
    }
    return newNode
        .setType((ArrayType) convert(node.getType()))
        .setDimensions(dimensions)
        .setInitializer((ArrayInitializer) convert(node.getInitializer()));
  }

  private static TreeNode convertArrayInitializer(org.eclipse.jdt.core.dom.ArrayInitializer node) {
    List<Expression> expressions = new ArrayList<>();
    for (Object expression : node.expressions()) {
      expressions.add((Expression) convert(expression));
    }
    ArrayInitializer newNode = new ArrayInitializer();
    convertExpression(node, newNode);
    return newNode
        .setTypeMirror(BindingConverter.getType(node.resolveTypeBinding()))
        .setExpressions(expressions);
  }

  private static TreeNode convertArrayType(org.eclipse.jdt.core.dom.ArrayType node) {
    ArrayType newNode = new ArrayType();
    convertType(node, newNode);

    // This could also be implemented as an element type and dimensions for JLS8, but we mainly deal
    // with ArrayTypes through the ArrayType(ITypeBinding) initializer, in the ArrayRewriter, for
    // which we use ITypeBinding's componentType anyway.
    Type componentType = (Type) Type.newType(node.resolveBinding().getComponentType());
    return newNode.setComponentType(componentType);
  }

  private static TreeNode convertAssertStatement(org.eclipse.jdt.core.dom.AssertStatement node) {
    return new AssertStatement()
        .setExpression((Expression) convert(node.getExpression()))
        .setMessage((Expression) convert(node.getMessage()));
  }

  private static TreeNode convertAssignment(org.eclipse.jdt.core.dom.Assignment node) {
    Assignment newNode = new Assignment();
    convertExpression(node, newNode);
    return newNode
        .setOperator(Assignment.Operator.fromJdtOperatorName(node.getOperator().toString()))
        .setLeftHandSide((Expression) convert(node.getLeftHandSide()))
        .setRightHandSide((Expression) convert(node.getRightHandSide()));
  }

  private static TreeNode convertBlock(org.eclipse.jdt.core.dom.Block node) {
    Block newNode = new Block();
    for (Object statement : node.statements()) {
      newNode.addStatement((Statement) convert(statement));
    }
    return newNode;
  }

  private static TreeNode convertBodyDeclaration(
      org.eclipse.jdt.core.dom.BodyDeclaration node, BodyDeclaration newNode) {
    List<Annotation> annotations = new ArrayList<>();
    for (Object modifier : node.modifiers()) {
      if (modifier instanceof org.eclipse.jdt.core.dom.Annotation) {
        annotations.add((Annotation) convert(modifier));
      }
    }
    return newNode
        .setModifiers(node.getModifiers())
        .setAnnotations(annotations)
        .setJavadoc((Javadoc) convert(node.getJavadoc()));
  }

  private static TreeNode convertBooleanLiteral(org.eclipse.jdt.core.dom.BooleanLiteral node) {
    return convertExpression(node, new BooleanLiteral(node.booleanValue(),
        BindingConverter.getType(node.resolveTypeBinding())));
  }

  private static TreeNode convertBreakStatement(org.eclipse.jdt.core.dom.BreakStatement node) {
    return new BreakStatement()
        .setLabel((SimpleName) convert(node.getLabel()));
  }

  private static TreeNode convertCastExpression(org.eclipse.jdt.core.dom.CastExpression node) {
    Type type = (Type) convert(node.getType());
    Expression expr = (Expression) convert(node.getExpression());
    CastExpression newNode = ((CastExpression) convertExpression(node, new CastExpression()))
        .setType(type)
        .setExpression(expr);

    // If we are casting a LambdaExpression, we set its type, as the JDT can
    // resolve to the wrong type for lambdas under certain circumstances
    // (casting to an intersection of two interfaces with a parameter-less lambda).
    if (expr instanceof LambdaExpression) {
      ((LambdaExpression) expr).setTypeMirror(type.getTypeMirror());
    }
    return newNode;
  }

  private static TreeNode convertCatchClause(org.eclipse.jdt.core.dom.CatchClause node) {
    return new CatchClause()
        .setBody((Block) convert(node.getBody()))
        .setException((SingleVariableDeclaration) convert(node.getException()));
  }

  private static TreeNode convertCharacterLiteral(org.eclipse.jdt.core.dom.CharacterLiteral node) {
    return convertExpression(node, new CharacterLiteral(node.charValue(),
        BindingConverter.getType(node.resolveTypeBinding())));
  }

  private static TreeNode convertConditionalExpression(
      org.eclipse.jdt.core.dom.ConditionalExpression node) {
    return new ConditionalExpression()
        .setTypeMirror(BindingConverter.getType(node.resolveTypeBinding()))
        .setExpression((Expression) convert(node.getExpression()))
        .setThenExpression((Expression) convert(node.getThenExpression()))
        .setElseExpression((Expression) convert(node.getElseExpression()));
  }

  private static TreeNode convertConstructorInvocation(
      org.eclipse.jdt.core.dom.ConstructorInvocation node) {
    ExecutableElement method =
        BindingConverter.getExecutableElement(node.resolveConstructorBinding());
    ConstructorInvocation newNode = new ConstructorInvocation()
        .setExecutableElement(method);
    for (Object argument : node.arguments()) {
      newNode.addArgument((Expression) convert(argument));
    }
    return newNode;
  }

  private static TreeNode convertContinueStatement(
      org.eclipse.jdt.core.dom.ContinueStatement node) {
    return new ContinueStatement()
        .setLabel((SimpleName) convert(node.getLabel()));
  }

  private static TreeNode convertDimension(org.eclipse.jdt.core.dom.Dimension node) {
    Dimension newNode = new Dimension();
    for (Object x : node.annotations()) {
      newNode.addAnnotation((Annotation) convert(x));
    }
    return newNode;
  }

  private static TreeNode convertDoStatement(org.eclipse.jdt.core.dom.DoStatement node) {
    return new DoStatement()
        .setExpression((Expression) convert(node.getExpression()))
        .setBody((Statement) convert(node.getBody()));
  }

  private static TreeNode convertEnhancedForStatement(
      org.eclipse.jdt.core.dom.EnhancedForStatement node) {
    return new EnhancedForStatement()
        .setParameter((SingleVariableDeclaration) convert(node.getParameter()))
        .setExpression((Expression) convert(node.getExpression()))
        .setBody((Statement) convert(node.getBody()));
  }

  private static TreeNode convertEnumConstantDeclaration(
      org.eclipse.jdt.core.dom.EnumConstantDeclaration node) {
    EnumConstantDeclaration newNode = new EnumConstantDeclaration();
    convertBodyDeclaration(node, newNode);
    newNode
        .setVariableBinding(BindingConverter.wrapBinding(node.resolveVariable()))
        .setExecutableElement(
            BindingConverter.getExecutableElement(node.resolveConstructorBinding()))
        .setName((SimpleName) convert(node.getName()))
        .setAnonymousClassDeclaration(
            (AnonymousClassDeclaration) convert(node.getAnonymousClassDeclaration()));
    for (Object argument : node.arguments()) {
      newNode.addArgument((Expression) convert(argument));
    }
    return newNode;
  }

  private static TreeNode convertEnumDeclaration(org.eclipse.jdt.core.dom.EnumDeclaration node) {
    EnumDeclaration newNode =
        (EnumDeclaration) convertAbstractTypeDeclaration(node, new EnumDeclaration());
    for (Object superInterface : node.superInterfaceTypes()) {
      newNode.addSuperInterfaceType((Type) convert(superInterface));
    }
    for (Object enumConstant : node.enumConstants()) {
      newNode.addEnumConstant((EnumConstantDeclaration) convert(enumConstant));
    }
    return newNode;
  }

  private static TreeNode convertExpression(
      org.eclipse.jdt.core.dom.Expression node, Expression newNode) {
    return newNode.setConstantValue(node.resolveConstantExpressionValue());
  }

  private static TreeNode convertExpressionStatement(
      org.eclipse.jdt.core.dom.ExpressionStatement node) {
    return new ExpressionStatement()
        .setExpression((Expression) convert(node.getExpression()));
  }

  private static TreeNode convertFieldAccess(org.eclipse.jdt.core.dom.FieldAccess node) {
    return new FieldAccess()
        .setVariableElement(BindingConverter.getVariableElement(node.resolveFieldBinding()))
        .setExpression((Expression) convert(node.getExpression()))
        .setName((SimpleName) convert(node.getName()));
  }

  private static TreeNode convertFieldDeclaration(org.eclipse.jdt.core.dom.FieldDeclaration node) {
    FieldDeclaration newNode = new FieldDeclaration();
    convertBodyDeclaration(node, newNode);
    for (Object fragment : node.fragments()) {
      newNode.addFragment((VariableDeclarationFragment) convert(fragment));
    }
    return newNode
        .setType((Type) convert(node.getType()));
  }

  private static TreeNode convertForStatement(org.eclipse.jdt.core.dom.ForStatement node) {
    ForStatement newNode = new ForStatement();
    for (Object initializer : node.initializers()) {
      newNode.addInitializer((Expression) convert(initializer));
    }
    for (Object updater : node.updaters()) {
      newNode.addUpdater((Expression) convert(updater));
    }
    return newNode
        .setExpression((Expression) convert(node.getExpression()))
        .setBody((Statement) convert(node.getBody()));
  }

  private static TreeNode convertIfStatement(org.eclipse.jdt.core.dom.IfStatement node) {
    return new IfStatement()
        .setExpression((Expression) convert(node.getExpression()))
        .setThenStatement((Statement) convert(node.getThenStatement()))
        .setElseStatement((Statement) convert(node.getElseStatement()));
  }

  private static TreeNode convertInfixExpression(org.eclipse.jdt.core.dom.InfixExpression node) {
    InfixExpression newNode = new InfixExpression();
    convertExpression(node, newNode);
    newNode
        .setTypeMirror(BindingConverter.getType(node.resolveTypeBinding()))
        .setOperator(Operator.parse(node.getOperator().toString()));

    // The JDT parser apparently does not always take advantage of extended
    // operands, resulting in potentially very deep trees that can overflow the
    // stack. This code traverses the subtree non-recursively and merges all
    // children that have the same operator into this node using extended
    // operands.
    List<StackState> stack = Lists.newArrayList();
    stack.add(new StackState(node));
    while (!stack.isEmpty()) {
      StackState currentState = stack.get(stack.size() - 1);
      org.eclipse.jdt.core.dom.Expression child = currentState.nextChild();
      if (child == null) {
        stack.remove(stack.size() - 1);
        continue;
      }
      if (child instanceof org.eclipse.jdt.core.dom.InfixExpression) {
        org.eclipse.jdt.core.dom.InfixExpression infixChild =
            (org.eclipse.jdt.core.dom.InfixExpression) child;
        if (infixChild.getOperator().equals(node.getOperator())) {
          stack.add(new StackState(infixChild));
          continue;
        }
      }
      newNode.addOperand((Expression) TreeConverter.convert(child));
    }
    return newNode;
  }

  private static TreeNode convertInitializer(org.eclipse.jdt.core.dom.Initializer node) {
    Initializer newNode = new Initializer();
    convertBodyDeclaration(node, newNode);
    return newNode
        .setBody((Block) convert(node.getBody()));
  }

  private static TreeNode convertInstanceofExpression(
      org.eclipse.jdt.core.dom.InstanceofExpression node) {
    return new InstanceofExpression()
        .setTypeMirror(BindingConverter.getType(node.resolveTypeBinding()))
        .setLeftOperand((Expression) convert(node.getLeftOperand()))
        .setRightOperand((Type) convert(node.getRightOperand()));
  }

  private static TreeNode convertIntersectionType(org.eclipse.jdt.core.dom.IntersectionType node) {
    JdtTypeMirror type = BindingConverter.getType(node.resolveBinding());
    IntersectionType newNode = new IntersectionType(type);
    for (Object x : node.types()) {
      newNode.addType((Type) convert(x));
    }
    return newNode;
  }

  private static TreeNode convertJavadoc(org.eclipse.jdt.core.dom.Javadoc node) {
    Javadoc newNode = new Javadoc();
    for (Object tag : node.tags()) {
      newNode.addTag((TagElement) convert(tag));
    }
    return newNode;
  }

  private static TreeNode convertLabeledStatement(org.eclipse.jdt.core.dom.LabeledStatement node) {
    return new LabeledStatement()
        .setLabel((SimpleName) convert(node.getLabel()))
        .setBody((Statement) convert(node.getBody()));
  }

  private static TreeNode convertAnnotation(org.eclipse.jdt.core.dom.Annotation node,
      Annotation newNode) {
    convertExpression(node, newNode);
    IAnnotationBinding binding = BindingConverter.wrapBinding(node.resolveAnnotationBinding());
    return newNode
        .setAnnotationMirror(new JdtAnnotationMirror(binding))
        .setTypeName((Name) convert(node.getTypeName()));
  }

  private static TreeNode convertMarkerAnnotation(org.eclipse.jdt.core.dom.MarkerAnnotation node) {
    Annotation newNode = null;
    if (node.getTypeName().getFullyQualifiedName().equals(Property.class.getSimpleName())) {
      newNode = new PropertyAnnotation();
      for (String attr : BindingUtil.parseAttributeString(node.resolveAnnotationBinding())) {
        ((PropertyAnnotation) newNode).addAttribute(attr);
      }
    } else {
      newNode = new MarkerAnnotation();
    }
    return convertAnnotation(node, newNode);
  }

  private static TreeNode convertMemberValuePair(org.eclipse.jdt.core.dom.MemberValuePair node) {
    return new MemberValuePair()
        .setName((SimpleName) convert(node.getName()))
        .setValue((Expression) convert(node.getValue()));
  }

  private static TreeNode convertNameQualifiedType(
      org.eclipse.jdt.core.dom.NameQualifiedType node) {
    JdtTypeMirror type = BindingConverter.getType(node.resolveBinding());
    return ((NameQualifiedType) convertAnnotatableType(node, new NameQualifiedType(type)))
        .setName((SimpleName) convert(node.getName()))
        .setQualifier((Name) convert(node.getQualifier()));
  }

  private static TreeNode convertNormalAnnotation(org.eclipse.jdt.core.dom.NormalAnnotation node) {
    NormalAnnotation newNode = new NormalAnnotation();
    for (Object value : node.values()) {
      newNode.addValue((MemberValuePair) convert(value));
    }
    return convertAnnotation(node, newNode);
  }

  private static TreeNode convertNumberLiteral(org.eclipse.jdt.core.dom.NumberLiteral node) {
    Object constantValue = node.resolveConstantExpressionValue();
    assert constantValue instanceof Number;
    Number value = (Number) constantValue;
    ITypeBinding typeBinding = BindingConverter.wrapBinding(node.resolveTypeBinding());
    return convertExpression(node, new NumberLiteral(value, BindingConverter.getType(typeBinding))
        .setToken(node.getToken()));
  }

  private static TreeNode convertPackageDeclaration(
      org.eclipse.jdt.core.dom.PackageDeclaration node) {
    PackageDeclaration newNode = new PackageDeclaration()
        .setName((Name) convert(node.getName()))
        .setPackageElement((PackageElement) BindingConverter.getPackageElement(node))
        .setJavadoc((Javadoc) convert(node.getJavadoc()));
    for (Object modifier : node.annotations()) {
      newNode.addAnnotation((Annotation) TreeConverter.convert(modifier));
    }
    return newNode;
  }

  private static TreeNode convertPrimitiveType(org.eclipse.jdt.core.dom.PrimitiveType node) {
    JdtTypeMirror type = BindingConverter.getType(node.resolveBinding());
    return convertAnnotatableType(node, new PrimitiveType(type));
  }

  private static TreeNode convertQualifiedType(org.eclipse.jdt.core.dom.QualifiedType node) {
    JdtTypeMirror type = BindingConverter.getType(node.resolveBinding());
    return convertAnnotatableType(node, new QualifiedType(type));
  }

  private static TreeNode convertSimpleType(org.eclipse.jdt.core.dom.SimpleType node) {
    JdtTypeMirror type = BindingConverter.getType(node.resolveBinding());
    return convertAnnotatableType(node, new SimpleType(type));
  }

  private static TreeNode convertSingleMemberAnnotation(
      org.eclipse.jdt.core.dom.SingleMemberAnnotation node) {
    Annotation newNode = null;
    if (node.getTypeName().getFullyQualifiedName().equals(Property.class.getSimpleName())) {
      newNode = new PropertyAnnotation();
      for (String attr : BindingUtil.parseAttributeString(node.resolveAnnotationBinding())) {
        ((PropertyAnnotation) newNode).addAttribute(attr);
      }
    } else {
      newNode = new SingleMemberAnnotation()
          .setValue((Expression) convert(node.getValue()));
    }
    return convertAnnotation(node, newNode);
  }

  private static TreeNode convertSuperFieldAccess(org.eclipse.jdt.core.dom.SuperFieldAccess node) {
    return new SuperFieldAccess()
        .setVariableElement(BindingConverter.getVariableElement(node.resolveFieldBinding()))
        .setQualifier((Name) convert(node.getQualifier()))
        .setName((SimpleName) convert(node.getName()));
  }

  private static TreeNode convertType(
      org.eclipse.jdt.core.dom.Type node, Type newNode) {
    return newNode.setTypeMirror(BindingConverter.getType(node.resolveBinding()));
  }

  private static TreeNode convertTypeDeclaration(org.eclipse.jdt.core.dom.TypeDeclaration node) {
    TypeDeclaration newNode =
        (TypeDeclaration) convertAbstractTypeDeclaration(node, new TypeDeclaration());
    newNode.setSuperclassType((Type) convert(node.getSuperclassType()));
    newNode.setInterface(node.isInterface());
    for (Object superInterface : node.superInterfaceTypes()) {
      newNode.addSuperInterfaceType((Type) convert(superInterface));
    }
    return newNode;
  }

  private static TreeNode convertWhileStatement(org.eclipse.jdt.core.dom.WhileStatement node) {
    return new WhileStatement()
        .setExpression((Expression) convert(node.getExpression()))
        .setBody((Statement) convert(node.getBody()));
  }

  // Helper class for convertInfixExpression().
  private static class StackState {
    private final org.eclipse.jdt.core.dom.InfixExpression expression;
    private int nextChild = -2;

    private StackState(org.eclipse.jdt.core.dom.InfixExpression expr) {
      expression = expr;
    }

    private org.eclipse.jdt.core.dom.Expression nextChild() {
      int childIdx = nextChild++;
      if (childIdx == -2) {
        return expression.getLeftOperand();
      } else if (childIdx == -1) {
        return expression.getRightOperand();
      } else if (childIdx < expression.extendedOperands().size()) {
        return (org.eclipse.jdt.core.dom.Expression) expression.extendedOperands().get(childIdx);
      } else {
        return null;
      }
    }
  }
}