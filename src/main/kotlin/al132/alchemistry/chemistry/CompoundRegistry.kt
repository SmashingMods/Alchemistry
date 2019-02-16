package al132.alchemistry.chemistry

import java.awt.Color

/**
 * Created by al132 on 1/22/2017.
 */
object CompoundRegistry {

    val compounds = ArrayList<ChemicalCompound>()

    fun init() {
        compounds.add(Compound {
            name = "cellulose"
            color = Color(121, 186, 88)
            components = listOf(
                    CompoundPair("carbon", 6),
                    CompoundPair("hydrogen", 10),
                    CompoundPair("oxygen", 5))
        })
        compounds.add(Compound
        {
            name = "silicon_dioxide"
            color = Color(165, 138, 45)
            components = listOf(
                    CompoundPair("silicon", 1),
                    CompoundPair("oxygen", 2))
        })
        compounds.add(Compound {
            name = "potassium_nitrate"
            color = Color(54, 102, 69)
            components = listOf(
                    CompoundPair("potassium", 1),
                    CompoundPair("nitrogen", 1),
                    CompoundPair("oxygen", 3))
        })
        compounds.add(Compound {
            name = "aluminum_oxide"
            color = Color(210, 226, 166)
            components = listOf(
                    CompoundPair("aluminum", 2),
                    CompoundPair("oxygen", 3))
        })
        compounds.add(Compound {
            name = "magnesium_oxide"
            color = Color(225, 164, 182)
            components = listOf(
                    CompoundPair("magnesium", 1),
                    CompoundPair("oxygen", 1))
        })

        compounds.add(Compound {
            name = "potassium_chloride"
            color = Color(236, 163, 32)
            components = listOf(
                    CompoundPair("potassium", 1),
                    CompoundPair("chlorine", 1))
        })
        compounds.add(Compound {
            name = "sodium_chloride"
            color = Color(219, 201, 216)
            components = listOf(
                    CompoundPair("sodium", 1),
                    CompoundPair("chlorine", 1))
        })
        compounds.add(Compound {
            name = "water"
            color = Color(17, 94, 192)
            components = listOf(
                    CompoundPair("hydrogen", 2),
                    CompoundPair("oxygen", 1))
        })
        compounds.add(Compound {
            name = "kaolinite"
            color = Color(164, 159, 218)
            components = listOf(
                    CompoundPair("aluminum_oxide", 1),
                    CompoundPair("silicon_dioxide", 2),
                    CompoundPair("water", 2))
        })
        compounds.add(Compound {
            //this formula is for cysteine technically... but having lots of amino acids in minechem was a pita
            //so I may just leave it like so. Chose cysteine because of the diversity of elements
            name = "protein"
            color = Color(144, 108, 21)
            autoDissolverRecipe = false
            components = listOf(
                    CompoundPair("carbon", 3),
                    CompoundPair("hydrogen", 7),
                    CompoundPair("nitrogen", 1),
                    CompoundPair("oxygen", 2),
                    CompoundPair("sulfur", 1))
        })
        compounds.add(Compound {
            name = "iron_oxide"
            color = Color(186, 49, 14)
            components = listOf(
                    CompoundPair("iron", 2),
                    CompoundPair("oxygen", 3))
        })
        compounds.add(Compound
        {
            name = "sucrose"
            color = Color(224, 213, 210)
            components = listOf(
                    CompoundPair("carbon", 12),
                    CompoundPair("hydrogen", 22),
                    CompoundPair("oxygen", 11))
        })

        compounds.add(Compound {
            name = "carbonate"
            color = Color(97, 113, 90)
            components = listOf(
                    CompoundPair("carbon", 1),
                    CompoundPair("oxygen", 3))
        })

        compounds.add(Compound {
            name = "calcium_carbonate"
            color = Color(254, 193, 129)
            components = listOf(
                    CompoundPair("calcium", 1),
                    CompoundPair("carbonate", 1))
        })
        compounds.add(Compound {
            name = "phosphate"
            color = Color(214, 210, 89)
            components = listOf(
                    CompoundPair("phosphorus", 1),
                    CompoundPair("oxygen", 4))
        })
        compounds.add(Compound {
            name = "hydroxide"
            color = Color(255, 174, 0)
            components = listOf(
                    CompoundPair("oxygen", 1),
                    CompoundPair("hydrogen", 1))
        })
        compounds.add(Compound {
            name = "hydroxylapatite"
            color = Color.white
            components = listOf(CompoundPair("calcium", 5),
                    CompoundPair("phosphate", 6),
                    CompoundPair("hydroxide", 2))
        })
        compounds.add(Compound {
            name = "strontium_carbonate"
            color = Color(242, 46, 75)
            components = listOf(
                    CompoundPair("strontium", 1),
                    CompoundPair("carbonate", 1))
        })
        compounds.add(Compound {
            name = "beryl"
            color = Color(177, 214, 191)
            components = listOf(
                    CompoundPair("beryllium", 3),
                    CompoundPair("aluminum", 2),
                    CompoundPair("silicon", 6),
                    CompoundPair("oxygen", 18))
        })
        compounds.add(Compound {
            name = "starch"
            color = Color(252, 239, 166)
            components = listOf(CompoundPair("carbon", 12),
                    CompoundPair("hydrogen", 20),
                    CompoundPair("oxygen", 10))
        })
        compounds.add(Compound {
            name = "cucurbitacin"
            color = Color.orange
            components = listOf(
                    CompoundPair("carbon", 32),
                    CompoundPair("hydrogen", 44),
                    CompoundPair("oxygen", 8))
        })

        compounds.add(Compound {
            name = "psilocybin"
            color = Color(80, 255, 80)
            components = listOf(
                    CompoundPair("carbon", 12),
                    CompoundPair("hydrogen", 17),
                    CompoundPair("nitrogen", 2),
                    CompoundPair("oxygen", 4),
                    CompoundPair("phosphorus", 1))
        })
        compounds.add(Compound {
            name = "zinc_oxide"
            color = Color.yellow
            components = listOf(
                    CompoundPair("zinc", 1),
                    CompoundPair("oxygen", 1))
        })
        compounds.add(Compound {
            name = "cobalt_aluminate"
            color = Color.blue
            components = listOf(
                    CompoundPair("cobalt", 1),
                    CompoundPair("aluminum", 2),
                    CompoundPair("oxygen", 4))
        })
        compounds.add(Compound {
            //linoleic acid is the formula used here, i'd rather not get pedantic and make 20 different fats
            name = "triglyceride"
            color = Color(200, 200, 90)
            components = listOf(
                    CompoundPair("carbon", 18),
                    CompoundPair("hydrogen", 32),
                    CompoundPair("oxygen", 2))
        })
        compounds.add(Compound {
            name = "lead_iodide"
            color = Color.yellow
            components = listOf(
                    CompoundPair("lead", 1),
                    CompoundPair("iodine", 2))
        })
        compounds.add(Compound {
            name = "ethanol"
            color = Color(210, 250, 150)
            components = listOf(
                    CompoundPair("carbon", 2),
                    CompoundPair("hydrogen", 5),
                    CompoundPair("hydroxide", 1)
            )
        })
        compounds.add(Compound {
            name = "amide"
            color = Color(210, 250, 250)
            components = listOf(
                    CompoundPair("nitrogen", 1),
                    CompoundPair("hydrogen", 2)
            )
        })
        compounds.add(Compound {
            name = "urea"
            color = Color(230, 240, 180)
            components = listOf(
                    CompoundPair("carbon", 1),
                    CompoundPair("oxygen", 1),
                    CompoundPair("amide", 2)
            )
        })
        compounds.add(Compound {
            name = "ammonium"
            color = Color(180, 250, 250)
            hasShiftedRecipe = true
            components = listOf(
                    CompoundPair("nitrogen", 1),
                    CompoundPair("hydrogen", 4)
            )
        })
        compounds.add(Compound {
            name = "diammonium_phosphate"
            color = Color(210, 250, 150)
            components = listOf(
                    CompoundPair("ammonium", 2),
                    CompoundPair("hydrogen", 1),
                    CompoundPair("phosphate", 1)
            )
        })
        compounds.add(Compound {
            name = "potassium_carbonate"
            color = Color(40, 210, 90)
            components = listOf(
                    CompoundPair("potassium", 2),
                    CompoundPair("carbonate", 1)
            )
        })
        compounds.add(Compound {
            name = "mescaline"
            color = Color(30, 30, 30)
            components = listOf(
                    CompoundPair("carbon", 11),
                    CompoundPair("hydrogen", 17),
                    CompoundPair("nitrogen", 1),
                    CompoundPair("oxygen", 3)
            )
        })
    }

    operator fun get(name: String) = compounds.find { it.name == name }

    operator fun get(index: Int): ChemicalCompound? = compounds[index]

    fun getMeta(name: String) = compounds.indexOfFirst { it.name == name } //indexOfFirst returns -1 if none match

    fun size(): Int = compounds.size
}