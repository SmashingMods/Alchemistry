package al132.alchemistry.chemistry

import java.awt.Color

/**
 * Created by al132 on 1/22/2017.
 */
object CompoundRegistry {
    private val compounds = HashMap<Int, ChemicalCompound>()
    var internalChemicalIndex = 0

    fun init() {
        addInternal(Compound {
            name = "cellulose"
            color = Color(121, 186, 88)
            components = listOf(
                    CompoundPair("carbon", 6),
                    CompoundPair("hydrogen", 10),
                    CompoundPair("oxygen", 5))
        })
        addInternal(Compound
        {
            name = "silicon_dioxide"
            color = Color(165, 138, 45)
            components = listOf(
                    CompoundPair("silicon", 1),
                    CompoundPair("oxygen", 2))
        })
        addInternal(Compound {
            name = "potassium_nitrate"
            color = Color(54, 102, 69)
            components = listOf(
                    CompoundPair("potassium", 1),
                    CompoundPair("nitrogen", 1),
                    CompoundPair("oxygen", 3))
        })
        addInternal(Compound {
            name = "aluminum_oxide"
            color = Color(210, 226, 166)
            components = listOf(
                    CompoundPair("aluminum", 2),
                    CompoundPair("oxygen", 3))
        })
        addInternal(Compound {
            name = "magnesium_oxide"
            color = Color(225, 164, 182)
            components = listOf(
                    CompoundPair("magnesium", 1),
                    CompoundPair("oxygen", 1))
        })

        addInternal(Compound {
            name = "potassium_chloride"
            color = Color(236, 163, 32)
            components = listOf(
                    CompoundPair("potassium", 1),
                    CompoundPair("chlorine", 1))
        })
        addInternal(Compound {
            name = "sodium_chloride"
            color = Color(219, 201, 216)
            components = listOf(
                    CompoundPair("sodium", 1),
                    CompoundPair("chlorine", 1))
        })
        addInternal(Compound {
            name = "water"
            color = Color(17, 94, 192)
            components = listOf(
                    CompoundPair("hydrogen", 2),
                    CompoundPair("oxygen", 1))
        })
        addInternal(Compound {
            name = "kaolinite"
            color = Color(164, 159, 218)
            components = listOf(
                    CompoundPair("aluminum_oxide", 1),
                    CompoundPair("silicon_dioxide", 2),
                    CompoundPair("water", 2))
        })
        addInternal(Compound {
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
        addInternal(Compound {
            name = "iron_oxide"
            color = Color(186, 49, 14)
            components = listOf(
                    CompoundPair("iron", 2),
                    CompoundPair("oxygen", 3))
        })
        addInternal(Compound
        {
            name = "sucrose"
            color = Color(224, 213, 210)
            shiftedSlots = 3
            components = listOf(
                    CompoundPair("carbon", 12),
                    CompoundPair("hydrogen", 22),
                    CompoundPair("oxygen", 11))
        })

        addInternal(Compound {
            name = "carbonate"
            color = Color(97, 113, 90)
            components = listOf(
                    CompoundPair("carbon", 1),
                    CompoundPair("oxygen", 3))
        })

        addInternal(Compound {
            name = "calcium_carbonate"
            color = Color(254, 193, 129)
            components = listOf(
                    CompoundPair("calcium", 1),
                    CompoundPair("carbonate", 1))
        })
        addInternal(Compound {
            name = "phosphate"
            color = Color(214, 210, 89)
            components = listOf(
                    CompoundPair("phosphorus", 1),
                    CompoundPair("oxygen", 4))
        })
        addInternal(Compound {
            name = "hydroxide"
            color = Color(255, 174, 0)
            components = listOf(
                    CompoundPair("oxygen", 1),
                    CompoundPair("hydrogen", 1))
        })
        addInternal(Compound {
            name = "hydroxylapatite"
            color = Color.white
            components = listOf(CompoundPair("calcium", 5),
                    CompoundPair("phosphate", 6),
                    CompoundPair("hydroxide", 2))
        })
        addInternal(Compound {
            name = "strontium_carbonate"
            color = Color(242, 46, 75)
            components = listOf(
                    CompoundPair("strontium", 1),
                    CompoundPair("carbonate", 1))
        })
        addInternal(Compound {
            name = "beryl"
            color = Color(177, 214, 191)
            components = listOf(
                    CompoundPair("beryllium", 3),
                    CompoundPair("aluminum", 2),
                    CompoundPair("silicon", 6),
                    CompoundPair("oxygen", 18))
        })
        addInternal(Compound {
            name = "starch"
            shiftedSlots = 6
            color = Color(252, 239, 166)
            components = listOf(CompoundPair("carbon", 12),
                    CompoundPair("hydrogen", 20),
                    CompoundPair("oxygen", 10))
        })
        addInternal(Compound {
            name = "cucurbitacin"
            color = Color.orange
            autoCombinerRecipe = false
            components = listOf(
                    CompoundPair("carbon", 32),
                    CompoundPair("hydrogen", 44),
                    CompoundPair("oxygen", 8))
        })

        addInternal(Compound {
            name = "psilocybin"
            color = Color(80, 255, 80)
            components = listOf(
                    CompoundPair("carbon", 12),
                    CompoundPair("hydrogen", 17),
                    CompoundPair("nitrogen", 2),
                    CompoundPair("oxygen", 4),
                    CompoundPair("phosphorus", 1))
        })
        addInternal(Compound {
            name = "zinc_oxide"
            color = Color.yellow
            components = listOf(
                    CompoundPair("zinc", 1),
                    CompoundPair("oxygen", 1))
        })
        addInternal(Compound {
            name = "cobalt_aluminate"
            color = Color.blue
            components = listOf(
                    CompoundPair("cobalt", 1),
                    CompoundPair("aluminum", 2),
                    CompoundPair("oxygen", 4))
        })
        addInternal(Compound {
            //linoleic acid is the formula used here, i'd rather not get pedantic and make 20 different fats
            name = "triglyceride"
            autoCombinerRecipe = false
            color = Color(200, 200, 90)
            components = listOf(
                    CompoundPair("carbon", 18),
                    CompoundPair("hydrogen", 32),
                    CompoundPair("oxygen", 2))
        })
        addInternal(Compound {
            name = "lead_iodide"
            color = Color.yellow
            components = listOf(
                    CompoundPair("lead", 1),
                    CompoundPair("iodine", 2))
        })
        addInternal(Compound {
            name = "ethanol"
            color = Color(210, 250, 150)
            components = listOf(
                    CompoundPair("carbon", 2),
                    CompoundPair("hydrogen", 5),
                    CompoundPair("hydroxide", 1)
            )
        })
        addInternal(Compound {
            name = "amide"
            color = Color(210, 250, 250)
            components = listOf(
                    CompoundPair("nitrogen", 1),
                    CompoundPair("hydrogen", 2)
            )
        })
        addInternal(Compound {
            name = "urea"
            color = Color(230, 240, 180)
            components = listOf(
                    CompoundPair("carbon", 1),
                    CompoundPair("oxygen", 1),
                    CompoundPair("amide", 2)
            )
        })
        addInternal(Compound {
            name = "ammonium"
            color = Color(180, 250, 250)
            shiftedSlots = 1
            components = listOf(
                    CompoundPair("nitrogen", 1),
                    CompoundPair("hydrogen", 4)
            )
        })
        addInternal(Compound {
            name = "diammonium_phosphate"
            color = Color(210, 250, 150)
            components = listOf(
                    CompoundPair("ammonium", 2),
                    CompoundPair("hydrogen", 1),
                    CompoundPair("phosphate", 1)
            )
        })
        addInternal(Compound {
            name = "potassium_carbonate"
            color = Color(40, 210, 90)
            components = listOf(
                    CompoundPair("potassium", 2),
                    CompoundPair("carbonate", 1)
            )
        })
        addInternal(Compound {
            name = "mescaline"
            color = Color(30, 30, 30)
            shiftedSlots = 1
            components = listOf(
                    CompoundPair("carbon", 11),
                    CompoundPair("hydrogen", 17),
                    CompoundPair("nitrogen", 1),
                    CompoundPair("oxygen", 3)
            )
        })
        addInternal(Compound {
            name = "mullite"
            color = Color(110, 110, 150)
            components = listOf(
                    CompoundPair("aluminum_oxide", 2),
                    CompoundPair("silicon_dioxide", 1)
            )
        })
        addInternal(Compound {
            name = "methane"
            color = Color(200, 30, 180)
            shiftedSlots = 1
            components = listOf(
                    CompoundPair("carbon", 1),
                    CompoundPair("hydrogen", 4)
            )
        })
        addInternal(Compound {
            name = "ethane"
            color = Color(200, 30, 50)
            shiftedSlots = 2
            components = listOf(
                    CompoundPair("carbon", 2),
                    CompoundPair("hydrogen", 6)
            )
        })
        addInternal(Compound {
            name = "propane"
            color = Color(100, 30, 50)
            shiftedSlots = 3
            components = listOf(
                    CompoundPair("carbon", 3),
                    CompoundPair("hydrogen", 8)
            )
        })
        addInternal(Compound {
            name = "butane"
            color = Color(111, 150, 180)
            shiftedSlots = 4
            components = listOf(
                    CompoundPair("carbon", 4),
                    CompoundPair("hydrogen", 10))
        })
        addInternal(Compound {
            name = "pentane"
            color = Color(111, 150, 85)
            shiftedSlots = 5
            components = listOf(
                    CompoundPair("carbon", 5),
                    CompoundPair("hydrogen", 12))
        })
        addInternal(Compound {
            name = "hexane"
            color = Color(111, 205, 50)
            shiftedSlots = 6
            components = listOf(
                    CompoundPair("carbon", 6),
                    CompoundPair("hydrogen", 14))
        })
        addInternal(Compound {
            name = "potassium_dichromate"
            color = Color(255, 150, 0)
            components = listOf(
                    CompoundPair("potassium", 2),
                    CompoundPair("chromium", 2),
                    CompoundPair("oxygen", 7))
        })
        addInternal(Compound {
            name = "nickel_chloride"
            color = Color(30, 255, 75)
            components = listOf(
                    CompoundPair("nickel", 1),
                    CompoundPair("chlorine", 2))
        })
        addInternal(Compound {
            name = "potassium_permanganate"
            color = Color(120, 0, 100)
            components = listOf(
                    CompoundPair("potassium", 1),
                    CompoundPair("manganese", 1),
                    CompoundPair("oxygen", 4))
        })
        addInternal(Compound {
            name = "magnesium_sulfate"
            color = Color(200, 250, 200)
            components = listOf(
                    CompoundPair("magnesium", 1),
                    CompoundPair("sulfur", 1),
                    CompoundPair("oxygen", 4))
        })
        addInternal(Compound {
            name = "copper_chloride"
            color = Color(100, 250, 200)
            components = listOf(
                    CompoundPair("copper", 1),
                    CompoundPair("chlorine", 2))
        })
        addInternal(Compound {
            name = "cadmium_sulfide"
            color = Color(250, 250, 94)
            components = listOf(
                    CompoundPair("cadmium", 1),
                    CompoundPair("sulfur", 1))
        })
        addInternal(Compound {
            name = "chromium_oxide"
            color = Color(150, 255, 150)
            components = listOf(
                    CompoundPair("chromium", 2),
                    CompoundPair("oxygen", 3))
        })
        addInternal(Compound {
            name = "antimony_trioxide"
            color = Color(250, 255, 200)
            components = listOf(
                    CompoundPair("antimony", 2),
                    CompoundPair("oxygen", 3))
        })
        addInternal(Compound {
            name = "titanium_oxide"
            color = Color(50, 30, 50)
            components = listOf(
                    CompoundPair("titanium", 2),
                    CompoundPair("oxygen", 3))
        })
        addInternal(Compound {
            name = "han_purple"
            color = Color(220, 100, 255)
            components = listOf(
                    CompoundPair("barium", 1),
                    CompoundPair("copper", 1),
                    CompoundPair("silicon", 2),
                    CompoundPair("oxygen", 6))
        })
        addInternal(Compound {
            name = "arsenic_sulfide"
            color = Color(250, 100, 130)
            components = listOf(
                    CompoundPair("arsenic", 4),
                    CompoundPair("sulfur", 4))
        })
        addInternal(Compound {
            name = "barium_sulfate"
            color = Color(220, 220, 255)
            components = listOf(
                    CompoundPair("barium", 1),
                    CompoundPair("sulfur", 1),
                    CompoundPair("oxygen", 4))
        })
        addInternal(Compound {
            name = "beta_carotene"
            color = Color(255, 140, 40)
            components = listOf(
                    CompoundPair("carbon", 40),
                    CompoundPair("hydrogen", 56))
        })
        addInternal(Compound {
            name = "polyvinyl_chloride"
            color = Color(95, 49, 40)
            components = listOf(
                    CompoundPair("carbon", 2),
                    CompoundPair("hydrogen", 3),
                    CompoundPair("chlorine", 1))
        })
        addInternal(Compound {
            name = "potassium_cyanide"
            color = Color(70, 250, 150)
            shiftedSlots = 1
            components = listOf(
                    CompoundPair("potassium", 1),
                    CompoundPair("carbon", 1),
                    CompoundPair("nitrogen", 1))
        })
        addInternal(Compound {
            name = "epinephrine"
            color = Color(230, 160, 120)
            shiftedSlots = 2
            components = listOf(
                    CompoundPair("carbon", 9),
                    CompoundPair("hydrogen", 13),
                    CompoundPair("nitrogen", 1),
                    CompoundPair("oxygen", 3))
        })
        addInternal(Compound {
            name = "cocaine"
            color = Color(210, 210, 255)
            shiftedSlots = 3
            components = listOf(
                    CompoundPair("carbon", 17),
                    CompoundPair("hydrogen", 21),
                    CompoundPair("nitrogen", 1),
                    CompoundPair("oxygen", 4))
        })
        addInternal(Compound {
            name = "acetylsalicylic_acid"
            color = Color(130, 190, 255)
            shiftedSlots = 5
            components = listOf(
                    CompoundPair("carbon", 9),
                    CompoundPair("hydrogen", 8),
                    CompoundPair("oxygen", 4))
        })
        addInternal(Compound {
            name = "penicillin"
            color = Color(255, 210, 210)
            shiftedSlots = 1
            components = listOf(
                    CompoundPair("carbon", 16),
                    CompoundPair("hydrogen", 18),
                    CompoundPair("nitrogen", 2),
                    CompoundPair("oxygen", 5),
                    CompoundPair("sulfur",1))
        })

    }

    private fun addInternal(compound: ChemicalCompound) {
        this.compounds[internalChemicalIndex] = compound
        internalChemicalIndex++
    }

    fun addExternal(meta: Int, _name: String, _color: Color, _components: List<CompoundPair>) {
        val temp = Compound {
            name = _name
            color = _color
            components = _components
            isInternalCompound = false
        }
        if (this.compounds[meta] == null) this.compounds[meta] = temp
    }

    operator fun get(name: String) = compounds.values.firstOrNull { it.name == name }

    operator fun get(index: Int): ChemicalCompound? = compounds[index]

    fun getMeta(name: String): Int = (compounds.entries.firstOrNull { it.value.name == name }?.key) ?: -1

    fun compounds(): Collection<ChemicalCompound> = this.compounds.values

    fun keys(): Collection<Int> = this.compounds.keys
}