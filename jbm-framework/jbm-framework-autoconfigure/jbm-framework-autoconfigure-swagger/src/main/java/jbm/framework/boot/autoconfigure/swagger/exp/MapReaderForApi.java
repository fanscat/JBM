package jbm.framework.boot.autoconfigure.swagger.exp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.jbm.framework.masterdata.controller.IMasterDataController;
import com.jbm.framework.masterdata.usage.form.PageRequestBody;
import com.jbm.framework.swagger.annotation.ApiJsonObject;
import com.jbm.framework.usage.form.BaseRequsetBody;
import com.jbm.framework.usage.paging.PageForm;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.WebMvcRequestHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static springfox.documentation.schema.ResolvedTypes.modelRefFactory;
import static springfox.documentation.schema.Types.isBaseType;
import static springfox.documentation.schema.Types.typeNameFor;
import static springfox.documentation.spi.schema.contexts.ModelContext.inputParam;

/**
 * @version 1.0
 * @Title: MapReaderForApi.java
 * @Description: 将map入参匹配到swagger文档的工具类
 */
@Order // plugin加载顺序，默认是最后加载
@Slf4j
public class MapReaderForApi implements ParameterBuilderPlugin {
    @Autowired
    private TypeResolver typeResolver;
    @Autowired
    private TypeNameExtractor nameExtractor;

    private final static String basePackage = "jbm.framework.boot.autoconfigure.swagger.exp."; // 动态生成的虚拟DTO Class的包路径

    @Override
    public void apply(ParameterContext context) {
        ResolvedMethodParameter methodParameter = context.resolvedMethodParameter();
        // 判断是否需要修改对象ModelRef,这里我判断的是Map类型和String类型需要重新修改ModelRef对象
        if (methodParameter.hasParameterAnnotation(RequestBody.class)) {
            Optional<ApiJsonObject> optional = methodParameter.findAnnotation(ApiJsonObject.class); // 根据参数上的ApiJsonObject注解中的参数动态生成Class
            if (optional.isPresent()) {
                this.addClassType(context, methodParameter, optional.get().type());
            } else {
                try {
                    //获取到Controller
                    Class controllerClass = getControllerClass(context);
                    Class entityType = getControllerEntityClass(controllerClass);
                    Class baseRequsetBodyType = methodParameter.getParameterType().getErasedType();
                    if (ObjectUtil.isNotEmpty(entityType) && ObjectUtil.isNotEmpty(baseRequsetBodyType)) {
                        if (BaseRequsetBody.class.isAssignableFrom(baseRequsetBodyType)) {
//                        if (ObjectUtil.isNotEmpty(methodParameter.getParameterType().getTypeParameters())) {
////                            entityType = methodParameter.getParameterType().getTypeParameters().get(0).getErasedType();
////                        }
                            if (PageRequestBody.class.isAssignableFrom(baseRequsetBodyType)) {
                                entityType = this.createRefModel(entityType.getSimpleName(), new ApiJsonPropertyBean(entityType, "实体类"), new ApiJsonPropertyBean(PageForm.class, "分页实体"));
                            }
                            this.addClassType(context, methodParameter, entityType);
                        }
                    }
                } catch (Exception e) {
                    log.error("解析参数错误", e);
                }
            }
        }
        //如果发现是form提交则改变请求类型
        if (methodParameter.hasParameterAnnotation(RequestParam.class)) {
            //只有post才处理
            if (context.getOperationContext().httpMethod().equals(HttpMethod.POST)) {
                context.getOperationContext().operationBuilder().consumes(Sets.newHashSet(MediaType.APPLICATION_FORM_URLENCODED_VALUE)).build();
            }
        }
    }


    /**
     * 获取Controller的Class信息
     *
     * @return
     */
    public Class getControllerEntityClass(Class controllerClass) {
        if (!IMasterDataController.class.isAssignableFrom(controllerClass))
            return null;
        Class entity = TypeUtil.getClass(TypeUtil.getTypeArgument(controllerClass));
        return entity;
    }

    /**
     * 获取Controller的Class信息
     *
     * @param context
     * @return
     */
    public Class getControllerClass(ParameterContext context) {
        RequestMappingContext requestMappingContext = (RequestMappingContext) BeanUtil.getFieldValue(context.getOperationContext(), "requestContext");
        WebMvcRequestHandler webMvcRequestHandler = (WebMvcRequestHandler) BeanUtil.getFieldValue(requestMappingContext, "handler");
        //获取到Controller
        Class controllerClass = webMvcRequestHandler.getHandlerMethod().getBeanType();
        return controllerClass;
    }

    /**
     * 根据propertys中的值动态生成含有Swagger注解的javaBeen
     */
    private Class createRefModel(String name, ApiJsonPropertyBean... propertys) throws NotFoundException, CannotCompileException {
        ClassPool pool = ClassPool.getDefault();
        final String className = basePackage + name + IdUtil.simpleUUID();
        CtClass ctClass = pool.makeClass(className);
        ConstPool constPool = ctClass.getClassFile().getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation ann = new Annotation("io.swagger.annotations.ApiModel", constPool);
        attr.addAnnotation(ann);
        ctClass.getClassFile().addAttribute(attr);
        for (ApiJsonPropertyBean property : propertys) {
            ctClass.addField(createField(property, ctClass));
        }
        return ctClass.toClass();
    }

    /**
     * 根据property的值生成含有swagger apiModelProperty注解的属性
     */
    private CtField createField(ApiJsonPropertyBean property, CtClass ctClass) throws NotFoundException, CannotCompileException {
        ClassPool pool = ClassPool.getDefault();
        CtClass fileType = pool.get(property.getType().getName());
        String fieldName = StrUtil.isBlank(property.getKey()) ? StrUtil.lowerFirst(property.getType().getSimpleName()) : property.getKey();
        CtField ctField = new CtField(fileType, fieldName, ctClass);
        ctField.setModifiers(Modifier.PUBLIC);
        ConstPool constPool = ctClass.getClassFile().getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation ann = new Annotation("io.swagger.annotations.ApiModelProperty", constPool);
        ann.addMemberValue("value", new StringMemberValue(property.getDescription(), constPool));
        attr.addAnnotation(ann);
        ctField.getFieldInfo().addAttribute(attr);

        return ctField;
    }

    public void addClassType(ParameterContext context, ResolvedMethodParameter methodParameter, Class type) {
        ResolvedType parameterType = typeResolver.resolve(type);
        parameterType = context.alternateFor(parameterType);
        context.getDocumentationContext().getAdditionalModels().add(parameterType);
        ModelReference modelRef = null;
        String typeName = typeNameFor(parameterType.getErasedType());
        if (isBaseType(typeName)) {
            modelRef = new ModelRef(typeName);
        }
//        else {
//            log.warn("Trying to infer dataType {}", parameterType);
//        }
        ModelContext modelContext = inputParam(
                context.getGroupName(),
                parameterType,
                context.getDocumentationType(),
                context.getAlternateTypeProvider(),
                context.getGenericNamingStrategy(),
                context.getIgnorableParameterTypes());
        context.parameterBuilder()
                .type(parameterType)
                .parameterType("body")
                .modelRef(Optional.fromNullable(modelRef)
                        .or(modelRefFactory(modelContext, nameExtractor).apply(parameterType)));
    }


    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }
}
