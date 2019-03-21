package al132.alchemistry.chemistry

import java.awt.Color
import java.util.*

/**
 * Created by al132 on 1/22/2017.
 */
object ElementRegistry {

    private val elements = HashMap<Int,ChemicalElement>()

    fun init() {
        //add(0, "placeholder", "?")
        add(1, "hydrogen", "H", Color.blue)
        add(2, "helium", "He", Color.red)
        add(3, "lithium", "Li", Color(40, 158, 86))
        add(4, "beryllium", "Be", Color(184, 199, 224))
        add(5, "boron", "B", Color(154, 176, 226))
        add(6, "carbon", "C", Color(59, 60, 63))
        add(7, "nitrogen", "N", Color(66, 123, 214))
        add(8, "oxygen", "O", Color(229, 220, 156))
        add(9, "fluorine", "F", Color(204, 186, 55))
        add(10, "neon", "Ne", Color(87, 229, 16))
        add(11, "sodium", "Na", Color(211, 198, 131))
        add(12, "magnesium", "Mg", Color(237, 178, 173))
        add(13, "aluminum", "Al", Color(247, 110, 69))
        add(14, "silicon", "Si", Color(173, 178, 121))
        add(15, "phosphorus", "P", Color(234, 98, 132))
        add(16, "sulfur", "S", Color(145, 158, 6))
        add(17, "chlorine", "Cl", Color(77, 102, 28))
        add(18, "argon", "Ar", Color(119, 117, 255))
        add(19, "potassium", "K", Color(198, 152, 95))
        add(20, "calcium", "Ca", Color(219, 210, 199))
        add(21, "scandium", "Sc", Color(252, 255, 99))
        add(22, "titanium", "Ti", Color(99, 255, 115))
        add(23, "vanadium", "V", Color(195, 186, 242))
        add(24, "chromium", "Cr", Color(236, 237, 218))
        add(25, "manganese", "Mn", Color(225, 186, 242))
        add(26, "iron", "Fe", Color.gray)
        add(27, "cobalt", "Co", Color(17, 114, 198))
        add(28, "nickel", "Ni", Color(198, 157, 162))
        add(29, "copper", "Cu", Color(255, 154, 30))
        add(30, "zinc", "Zn", Color(189, 196, 141))
        add(31, "gallium", "Ga", Color(122, 20, 49))
        add(32, "germanium", "Ge", Color(104, 172, 255))
        add(33, "arsenic", "As", Color(62, 145, 76))
        add(34, "selenium", "Se", Color(116, 62, 145))
        add(35, "bromine", "Br", Color(77, 160, 0))
        add(36, "krypton", "Kr", Color(229, 151, 50))
        add(37, "rubidium", "Rb", Color(15, 61, 40))
        add(38, "strontium", "Sr", Color(206, 88, 24))
        add(39, "yttrium", "Y", Color(206, 179, 24))
        add(40, "zirconium", "Zr", Color(127, 80, 22))
        add(41, "niobium", "Nb", Color(2, 29, 255))
        add(42, "molybdenum", "Mo", Color(39, 0, 48))
        add(43, "technetium", "Tc", Color(72, 170, 63))
        add(44, "ruthenium", "Ru", Color(255, 240, 86))
        add(45, "rhodium", "Rh", Color(255, 0, 80))
        add(46, "palladium", "Pd", Color(0, 255, 169))
        add(47, "silver", "Ag", Color(226, 217, 206))
        add(48, "cadmium", "Cd", Color(160, 147, 115))
        add(49, "indium", "In", Color(163, 230, 255))
        add(50, "tin", "Sn", Color(132, 161, 206))
        add(51, "antimony", "Sb", Color(193, 40, 58))
        add(52, "tellurium", "Te", Color(39, 91, 26))
        add(53, "iodine", "I", Color(62, 17, 63))
        add(54, "xenon", "Xe", Color(196, 51, 204))
        add(55, "cesium", "Cs", Color(255, 148, 0))
        add(56, "barium", "Ba", Color(0, 219, 179))
        add(57, "lanthanum", "La", Color(188, 253, 255))
        add(58, "cerium", "Ce", Color(255, 254, 211))
        add(59, "praseodymium", "Pr", Color(255, 161, 0))
        add(60, "neodymium", "Nd", Color(38, 28, 11))
        add(61, "promethium", "Pm", Color(105, 175, 123))
        add(62, "samarium", "Sm", Color(73, 69, 73))
        add(63, "europium", "Eu", Color(27, 211, 45))
        add(64, "gadolinium", "Gd", Color(123, 50, 208))
        add(65, "terbium", "Tb", Color(3, 37, 118))
        add(66, "dysprosium", "Dy", Color(73, 0, 219))
        add(67, "holmium", "Ho", Color(62, 255, 56))
        add(68, "erbium", "Er", Color(194, 214, 215))
        add(69, "thulium", "Tm", Color(234, 178, 178))
        add(70, "ytterbium", "Yb", Color(255, 76, 219))
        add(71, "lutetium", "Lu", Color(175, 0, 219))
        add(72, "hafnium", "Hf", Color(69, 81, 233))
        add(73, "tantalum", "Ta", Color(108, 142, 110))
        add(74, "tungsten", "W", Color(120, 128, 140))
        add(75, "rhenium", "Re", Color(199, 226, 89))
        add(76, "osmium", "Os", Color(102, 129, 173))
        add(77, "iridium", "Ir", Color(215, 242, 238))
        add(78, "platinum", "Pt", Color(114, 202, 229))
        add(79, "gold", "Au", Color.yellow)
        add(80, "mercury", "Hg", Color(160, 159, 157))
        add(81, "thallium", "Tl", Color(103, 50, 25))
        add(82, "lead", "Pb", Color(186, 135, 193))
        add(83, "bismuth", "Bi", Color(252, 171, 40))
        add(84, "polonium", "Po", Color(138, 87, 85))
        add(85, "astatine", "At", Color(120, 128, 213))
        add(86, "radon", "Rn", Color(76, 66, 179))
        add(87, "francium", "Fr", Color(81, 114, 198))
        add(88, "radium", "Ra", Color(255, 181, 221))
        add(89, "actinium", "Ac", Color(14, 182, 145))
        add(90, "thorium", "Th", Color(56, 79, 75))
        add(91, "protactinium", "Pa", Color(204, 233, 2))
        add(92, "uranium", "U", Color(93, 178, 19))
        add(93, "neptunium", "Np", Color(32, 20, 158))
        add(94, "plutonium", "Pu", Color(211, 211, 209))
        add(95, "americium", "Am", Color(237, 124, 75))
        add(96, "curium", "Cm", Color(229, 110, 149))
        add(97, "berkelium", "Bk", Color(44, 66, 49))
        add(98, "californium", "Cf", Color(175, 182, 16))
        add(99, "einsteinium", "Es", Color(192, 210, 95))
        add(100, "fermium", "Fm", Color(74, 226, 83))
        add(101, "mendelevium", "Md", Color(175, 176, 249))
        add(102, "nobelium", "No", Color(94, 44, 52))
        add(103, "lawrencium", "Lr", Color(216, 45, 92))
        add(104, "rutherfordium", "Rf", Color(240, 61, 22))
        add(105, "dubnium", "Db", Color(11, 112, 108))
        add(106, "seaborgium", "Sg", Color(158, 49, 74))
        add(107, "bohrium", "Bh", Color(166, 251, 51))
        add(108, "hassium", "Hs", Color(78, 5, 51))
        add(109, "meitnerium", "Mt", Color(169, 138, 37))
        add(110, "darmstadtium", "Ds", Color(14, 144, 190))
        add(111,"roentgenium","Rg",Color(150,90,90))
        add(112,"copernicium","Cn",Color(160,40,240))
        add(113,"nihonium","Nh",Color(220,250,180))
        add(114,"flerovium","Fl",Color(200,180,254))
        add(115,"moscovium","Mc",Color(250,180,200))
        add(116,"livermorium","Lv",Color(250,250,200))
        add(117,"tennessine","Ts",Color(150,250,250))
        add(118,"oganesson","Og",Color(250,150,250))
    }

    operator fun get(atomicNumber: Int): ChemicalElement? = elements.values.firstOrNull { it.meta == atomicNumber }

    operator fun get(elementName: String): ChemicalElement? = elements.values.firstOrNull { it.name == elementName }

    fun add(atomicNumber: Int, name: String, abbreviation: String, color: Color = Color.white): Boolean {
        if(elements.containsKey(atomicNumber)) return false
        else {
            elements[atomicNumber] = ChemicalElement(name, abbreviation, color)
            return true
        }
    }

    //fun size(): Int = elements.size

    fun getMeta(name: String): Int = elements.entries.firstOrNull { it.value.name == name }?.key ?: -1

    fun getAllElements() = this.elements.values

    fun keys() = this.elements.keys
}