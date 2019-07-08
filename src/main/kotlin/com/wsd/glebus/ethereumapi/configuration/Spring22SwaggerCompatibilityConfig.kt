package com.wsd.glebus.ethereumapi.configuration

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.types.ResolvedArrayType
import com.fasterxml.classmate.types.ResolvedObjectType
import com.fasterxml.classmate.types.ResolvedPrimitiveType
import com.google.common.base.Optional.fromNullable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.plugin.core.PluginRegistry
import springfox.documentation.schema.Collections.containerType
import springfox.documentation.schema.Collections.isContainerType
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.ModelNameContext
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.Types.typeNameFor
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.EnumTypeDeterminer
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.schema.contexts.ModelContext
import springfox.documentation.spi.service.DefaultsProviderPlugin
import springfox.documentation.spi.service.ResourceGroupingStrategy
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder
import springfox.documentation.spring.web.SpringGroupingStrategy
import springfox.documentation.spring.web.plugins.DefaultConfiguration
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager
import java.lang.reflect.Type


@Configuration
@Deprecated("Remove after https://github.com/springfox/springfox/issues/2932 fixed")
class Spring22SwaggerCompatibilityConfig {

    @Primary
    @Bean
    fun documentationPluginsManagerBootAdapter(): DocumentationPluginsManagerBootAdapter {
        return DocumentationPluginsManagerBootAdapter()
    }

    @Primary
    @Bean
    fun typeNameExtractorBootAdapter(typeResolver: TypeResolver,
                                     typeNameProviders: PluginRegistry<TypeNameProviderPlugin, DocumentationType>,
                                     enumTypeDeterminer: EnumTypeDeterminer): TypeNameExtractorBootAdapter {
        return TypeNameExtractorBootAdapter(typeResolver, typeNameProviders, enumTypeDeterminer);
    }

    class DocumentationPluginsManagerBootAdapter : DocumentationPluginsManager() {
        @Autowired
        @Qualifier("resourceGroupingStrategyRegistry")
        lateinit var resourceGroupingStrategies: PluginRegistry<ResourceGroupingStrategy, DocumentationType>

        @Autowired
        @Qualifier("defaultsProviderPluginRegistry")
        lateinit var defaultsProviders: PluginRegistry<DefaultsProviderPlugin, DocumentationType>

        override fun resourceGroupingStrategy(documentationType: DocumentationType): ResourceGroupingStrategy {
            return resourceGroupingStrategies.getPluginOrDefaultFor(documentationType, SpringGroupingStrategy())
        }

        override fun createContextBuilder(documentationType: DocumentationType,
                                          defaultConfiguration: DefaultConfiguration): DocumentationContextBuilder {
            return defaultsProviders.getPluginOrDefaultFor(documentationType, defaultConfiguration)
                    .create(documentationType).withResourceGroupingStrategy(resourceGroupingStrategy(documentationType))
        }
    }

    class TypeNameExtractorBootAdapter(private val typeResolver: TypeResolver,
                                       private val typeNameProviders: PluginRegistry<TypeNameProviderPlugin, DocumentationType>,
                                       private val enumTypeDeterminer: EnumTypeDeterminer) : TypeNameExtractor(typeResolver, typeNameProviders, enumTypeDeterminer) {

        override fun typeName(context: ModelContext): String? {
            val type = asResolved(context.type)
            return if (isContainerType(type)) {
                containerType(type)
            } else innerTypeName(type, context)
        }

        private fun asResolved(type: Type): ResolvedType {
            return typeResolver.resolve(type)
        }

        private fun genericTypeName(resolvedType: ResolvedType, context: ModelContext): String {
            val erasedType = resolvedType.erasedType
            val namingStrategy = context.genericNamingStrategy
            val nameContext = ModelNameContext(resolvedType.erasedType,
                    context.documentationType)
            val simpleName = fromNullable(typeNameFor(erasedType)).or(typeName(nameContext))
            val sb = StringBuilder(String.format("%s%s", simpleName, namingStrategy.openGeneric))
            var first = true
            for (index in 0 until erasedType.typeParameters.size) {
                val typeParam = resolvedType.typeParameters[index]
                if (first) {
                    sb.append(innerTypeName(typeParam, context))
                    first = false
                } else {
                    sb.append(String.format("%s%s", namingStrategy.typeListDelimiter,
                            innerTypeName(typeParam, context)))
                }
            }
            sb.append(namingStrategy.closeGeneric)
            return sb.toString()
        }

        private fun innerTypeName(type: ResolvedType, context: ModelContext): String? {
            return if (type.typeParameters.size > 0 && type.erasedType.typeParameters.isNotEmpty()) {
                genericTypeName(type, context)
            } else simpleTypeName(type, context)
        }

        private fun simpleTypeName(type: ResolvedType, context: ModelContext): String? {
            val erasedType = type.erasedType
            if (type is ResolvedPrimitiveType) {
                return typeNameFor(erasedType)
            } else if (enumTypeDeterminer.isEnum(erasedType)) {
                return "string"
            } else if (type is ResolvedArrayType) {
                val namingStrategy = context.genericNamingStrategy
                return String.format("Array%s%s%s", namingStrategy.openGeneric,
                        simpleTypeName(type.getArrayElementType(), context), namingStrategy.closeGeneric)
            } else if (type is ResolvedObjectType) {
                val typeName = typeNameFor(erasedType)
                if (typeName != null) {
                    return typeName
                }
            }
            return typeName(ModelNameContext(type.erasedType, context.documentationType))
        }

        private fun typeName(context: ModelNameContext): String {
            val selected = typeNameProviders.getPluginOrDefaultFor(context.documentationType,
                    DefaultTypeNameProvider())
            return selected.nameFor(context.type)
        }
    }
}