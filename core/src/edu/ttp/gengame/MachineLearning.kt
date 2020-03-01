package edu.ttp.gengame

import java.util.ArrayList
import java.util.Arrays
import java.util.function.Supplier
import kotlin.math.roundToInt
import kotlin.reflect.full.memberProperties

 class MachineLearning {
     class GeneticAlgorithm {
         object ManageRound {
             class GenerationState(val counter: Int, val generation: Array<Def>)

            fun generationZero(): GenerationState {
                val generationSize = GenerationConfig.generationSize
                val cwCarGeneration = Array(generationSize) { k ->
                    val def = CreateInstance.createGenerationZero(Supplier { Game.random.nextDouble() })
                    def.index = k
                    def
                }
                return GenerationState(0, cwCarGeneration)
            }

            fun nextGeneration(
                    previousState: GenerationState,
                    scores: List<CarRunner>
            ): GenerationState {
                val championLength = GenerationConfig.championLength
                val generationSize = GenerationConfig.generationSize
                val newGeneration = arrayOfNulls<Def>(generationSize)
                var newborn: Def
                for (k in 0 until championLength) {
                    scores[k].def.isElite = true
                    scores[k].def.index = k
                    newGeneration[k] = scores[k].def
                }
                val parentList = ArrayList<IntArray>()
                for (k in championLength until generationSize) {
                    val parent1 = GenerationConfig.selectFromAllParents(scores, parentList, null)
                    var parent2 = parent1
                    while (parent2 == parent1) {
                        parent2 = GenerationConfig.selectFromAllParents(scores, parentList, parent1)
                    }
                    val pair = intArrayOf(parent1, parent2)

                    parentList.add(pair)
                    newborn = makeChild(
                            pair.map { parent -> scores[parent].def }.toTypedArray()
                    )
                    newborn = mutate(newborn)
                    newborn.isElite = false
                    newborn.index = k
                    newGeneration[k] = newborn
                }

                return GenerationState(previousState.counter + 1, newGeneration.requireNoNulls())
            }

            private fun makeChild(parents: Array<Def>): Def {
                return CreateInstance.createCrossBreed(parents)
            }

            private fun mutate(parent: Def): Def {
                val schema = CarSchema.Schema
                val mutationRange = GenerationConfig.mutation_range
                val genMutation = GenerationConfig.gen_mutation
                val generateRandom = Supplier { GenerationConfig.generateRandom() }
                return CreateInstance.createMutatedClone(
                        schema,
                        generateRandom,
                        parent,
                        mutationRange, // Math.max(mutation_range), - ???
                        genMutation
                )
            }
        }
    }

     class Random {
        //  shuffleIntegers(prop, generator){
        //    return random.mapToShuffle(prop, random.createNormals({
        //      length: prop.length || 10,
        //      inclusive: true,
        //    }, generator));
        //  },
        fun shuffleIntegers(prop: SchemaElement, generator: Supplier<Double>): List<Double> {
            return mapToShuffle(prop, createNormals(
                    SchemaElement(length=prop.length ?: 10,inclusive=true),
                    generator))
        }

        //  createIntegers(prop, generator){
        //    return random.mapToInteger(prop, random.createNormals({
        //      length: prop.length,
        //      inclusive: true,
        //    }, generator));
        //  },
        fun createIntegers(prop: SchemaElement, generator: Supplier<Double>): List<Double> {
            return mapToInteger(prop, createNormals(
                    SchemaElement(length=prop.length,inclusive=true),
                    generator))
        }

        //  createFloats(prop, generator){
        //    return random.mapToFloat(prop, random.createNormals({
        //      length: prop.length,
        //      inclusive: true,
        //    }, generator));
        //  },
        fun createFloats(prop: SchemaElement, generator: Supplier<Double>): List<Double> {
            return mapToFloat(prop, createNormals(
                    SchemaElement(length=prop.length,inclusive=true),
                    generator))
        }

        //  mutateShuffle(
        //    prop, generator, originalValues, mutation_range, chanceToMutate
        //  ){
        //    return random.mapToShuffle(prop, random.mutateNormals(
        //      prop, generator, originalValues, mutation_range, chanceToMutate
        //    ));
        //  },
        fun mutateShuffle(prop: SchemaElement, generator: Supplier<Double>, originalValues: DoubleArray,
                          mutation_range: Double, chanceToMutate: Double): List<Double> {
            return mapToShuffle(prop, mutateNormals(
                    prop, generator, originalValues, mutation_range, chanceToMutate
            ))
        }

        //  mutateIntegers(prop, generator, originalValues, mutation_range, chanceToMutate){
        //    return random.mapToInteger(prop, random.mutateNormals(
        //      prop, generator, originalValues, mutation_range, chanceToMutate
        //    ));
        //  },
        fun mutateIntegers(prop: SchemaElement, generator: Supplier<Double>, originalValues: DoubleArray,
                           mutation_range: Double, chanceToMutate: Double): List<Double> {
            return mapToInteger(prop, mutateNormals(
                    prop, generator, originalValues, mutation_range, chanceToMutate
            ))
        }

        //  mutateFloats(prop, generator, originalValues, mutation_range, chanceToMutate){
        //    return random.mapToFloat(prop, random.mutateNormals(
        //      prop, generator, originalValues, mutation_range, chanceToMutate
        //    ));
        //  },
        fun mutateFloats(prop: SchemaElement, generator: Supplier<Double>, originalValues: DoubleArray,
                         mutation_range: Double, chanceToMutate: Double): List<Double> {
            return mapToFloat(prop, mutateNormals(
                    prop, generator, originalValues, mutation_range, chanceToMutate
            ))
        }

        companion object {

            //  createNormals(prop, generator){
            //    var l = prop.length;
            //    var values = [];
            //    for(var i = 0; i < l; i++){
            //      values.push(
            //        createNormal(prop, generator)
            //      );
            //    }
            //    return values;
            //  },
            fun createNormals(prop: SchemaElement, generator: Supplier<Double>): List<Double> {
                return List(prop.length!!) { createNormal(prop, generator)}
            }

            //  mapToShuffle(prop, normals){
            //    var offset = prop.offset || 0;
            //    var limit = prop.limit || prop.length;
            //    var sorted = normals.slice().sort(function(a, b){
            //      return a - b;
            //    });
            //    return normals.map(function(val){
            //      return sorted.indexOf(val);
            //    }).map(function(i){
            //      return i + offset;
            //    }).slice(0, limit);
            //  },
            fun mapToShuffle(prop: SchemaElement, normals: List<Double>): List<Double> {
                val offset = prop.offset ?: 0
                val limit = prop.limit ?: prop.length

                //    var sorted = normals.slice().sort((a, b) -> a - b); // ??? slice, sort a - b

                return normals.map { `val` -> (normals.sorted().indexOf(`val`) + offset).toDouble() }.take(limit!!)
            }

            //  mapToInteger(prop, normals){
            //    prop = {
            //      min: prop.min || 0,
            //      range: prop.range || 10,
            //      length: prop.length
            //    }
            //    return random.mapToFloat(prop, normals).map(function(float){
            //      return Math.round(float);
            //    });
            //  },
            fun mapToInteger(prop: SchemaElement, normals: List<Double>): List<Double> {
                val newProp = SchemaElement(
                        min = prop.min ?: 0.0,
                        range = prop.range ?: 10.0,
                        length = prop.length!!
                )

                return mapToFloat(newProp, normals).map { it.roundToInt().toDouble() }
            }

            //  mapToFloat(prop, normals){
            //    prop = {
            //      min: prop.min || 0,
            //      range: prop.range || 1
            //    }
            //    return normals.map(function(normal){
            //      var min = prop.min;
            //      var range = prop.range;
            //      return min + normal * range
            //    })
            //  },
            fun mapToFloat(prop: SchemaElement, normals: List<Double>): List<Double> {
                return normals.map { normal ->
                    (prop.min ?: 0.0) + normal * (prop.range ?: 1.0)
                }
            }

            //  mutateNormals(prop, generator, originalValues, mutation_range, chanceToMutate){
            //    var factor = (prop.factor || 1) * mutation_range
            //    return originalValues.map(function(originalValue){
            //      if(generator() > chanceToMutate){
            //        return originalValue;
            //      }
            //      return mutateNormal(
            //        prop, generator, originalValue, factor
            //      );
            //    });
            //  }
            fun mutateNormals(prop: SchemaElement, generator: Supplier<Double>, originalValues: DoubleArray,
                              mutation_range: Double, chanceToMutate: Double): List<Double> {
                val factor = (prop.factor ?: 1) * mutation_range
                return originalValues.map { originalValue: Double -> if (generator.get() > chanceToMutate) originalValue else mutateNormal(prop, generator, originalValue, factor) }
            }

            //function mutateNormal(prop, generator, originalValue, mutation_range){
            //  if(mutation_range > 1){
            //    throw new Error("Cannot mutate beyond bounds");
            //  }
            //  var newMin = originalValue - 0.5;
            //  if (newMin < 0) newMin = 0;
            //  if (newMin + mutation_range  > 1)
            //    newMin = 1 - mutation_range;
            //  var rangeValue = createNormal({
            //    inclusive: true,
            //  }, generator);
            //  return newMin + rangeValue * mutation_range;
            //}
            private fun mutateNormal(prop: SchemaElement, generator: Supplier<Double>, originalValue: Double,
                                     mutation_range: Double): Double {
                if (mutation_range > 1) {
                    throw Error("Cannot mutate beyond bounds")
                }
                var newMin = originalValue - 0.5
                if (newMin < 0) newMin = 0.0
                if (newMin + mutation_range > 1)
                    newMin = 1 - mutation_range
                val rangeValue = createNormal(SchemaElement(inclusive=true), generator)
                return newMin + rangeValue * mutation_range
            }


            // function createNormal(prop, generator){
            //  if(!prop.inclusive){
            //    return generator();
            //  } else {
            //    return generator() < 0.5 ?
            //    generator() :
            //    1 - generator();
            //  }
            // }
            private fun createNormal(prop: SchemaElement, generator: Supplier<Double>): Double {
                return if (prop.inclusive != true) {
                    generator.get()
                } else {
                    if (generator.get() < 0.5) generator.get() else 1 - generator.get() // ???
                }
            }
        }
    }

     object CreateInstance {
        fun createGenerationZero(generator: Supplier<Double>): Def {
            val instance = Def(Game.random.nextInt())

            for (schemaKey in CarSchema.Schema::class.memberProperties) {
                try {
                    val schemaProp = schemaKey.get(CarSchema.Schema) as SchemaElement
                    val values = Random.createNormals(schemaProp, generator)
                    val defKey = Def::class.java.getField(schemaKey.name)
                    defKey.set(instance, values)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                }

            }
            return instance
        }

        fun createCrossBreed(parents: Array<Def>): Def {
            val id = Game.random.nextInt()
            val crossDef = Def(id, parents.map { parent -> Def(parent.id, parent.ancestry) })
            CarSchema.Schema::class.memberProperties.forEach { schemaKey ->
                try {
                    val defKey = Def::class.java.getField(schemaKey.name)
                    val schemaDef = schemaKey.get(CarSchema.Schema) as SchemaElement

                    val values = DoubleArray(schemaDef.length!!)
                    var i = 0
                    val l = schemaDef.length
                    while (i < l) {
                        val p = GenerationConfig.pickParent(id, defKey.name, parents)
                        values[i] = (defKey.get(parents[p]) as DoubleArray)[i]
                        i++
                    }
                    defKey.set(crossDef, values)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                }
            }
            return crossDef
        }

        //    createMutatedClone(schema, generator, parent, factor, chanceToMutate){
        //        return Object.keys(schema).reduce(function(clone, key){
        //            var schemaProp = schema[key];
        //            var originalValues = parent[key];
        //            var values = random.mutateNormals(
        //                    schemaProp, generator, originalValues, factor, chanceToMutate
        //            );
        //            clone[key] = values;
        //            return clone;
        //        }, {
        //            id: parent.id,
        //                    ancestry: parent.ancestry
        //        });
        //    },
        fun createMutatedClone(schema: CarSchema.Schema, generator: Supplier<Double>, parent: Def, factor: Int, chanceToMutate: Double): Def {
            val clone = Def(parent.id, parent.ancestry)
            Arrays.stream(CarSchema.Schema::class.java.fields).forEach { schemaKey ->
                try {
                    val defKey = Def::class.java.getField(schemaKey.name)
                    val schemaProp = schemaKey.get(schema) as SchemaElement
                    val originalValues = defKey.get(parent) as DoubleArray
                    val values = Random.mutateNormals(
                            schemaProp, generator, originalValues, factor.toDouble(), chanceToMutate
                    )
                    defKey.set(clone, values)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                }
            }
            return clone
        }

        @Suppress("UNCHECKED_CAST")
        fun applyTypes(parent: Def): Def {
            val res = Def(parent.id, parent.ancestry)
            CarSchema.Schema::class.memberProperties.forEach { schema_field ->
                try {
                    val schemaProp = schema_field.get(Game.WordDef.schema) as SchemaElement
                    val defField = Def::class.java.getField(schema_field.name)

                    val originalValues = defField.get(parent) as List<Double>

                    val values: List<Double>
                    values = when (schemaProp.type) {
                        "shuffle" -> Random.mapToShuffle(schemaProp, originalValues)
                        "float" -> Random.mapToFloat(schemaProp, originalValues)
                        "integer" -> Random.mapToInteger(schemaProp, originalValues) // ??? float
                        else -> throw Error("Unknown type " + schemaProp.type + " of schema for key \${key}")
                    }

                    defField.set(res, values)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                }
            }
            return res
        }
    }
}