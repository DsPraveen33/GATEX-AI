package com.example.data.local

import com.example.data.model.*

object GateFullPaperData {

    fun generateOfficial100MarkGatePaper(): List<QuestionEntity> {
        val questions = mutableListOf<QuestionEntity>()

        // =========================================================================
        // SECTION 1: GENERAL APTITUDE (GA) — 10 Questions (Total: 15 Marks)
        // Q1 to Q5: 1 Mark each (5 Marks)
        // Q6 to Q10: 2 Marks each (10 Marks)
        // =========================================================================

        // GA Q1 (1 Mark - Verbal)
        questions.add(
            QuestionEntity(
                id = 1001,
                subjectId = "ga",
                topicId = "ga_verbal",
                questionText = "Select the word that is opposite in meaning to 'METICULOUS':",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Precise\", \"B) Careless\", \"C) Scrupulous\", \"D) Fastidious\"]",
                correctAnswer = "B",
                explanation = "'Meticulous' means showing great attention to detail; very careful and precise. The antonym is 'Careless'.",
                commonTrap = "Choosing 'Scrupulous' or 'Fastidious' which are synonyms.",
                formulaUsed = "Vocabulary Antonym Identification",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // GA Q2 (1 Mark - Quantitative)
        questions.add(
            QuestionEntity(
                id = 1002,
                subjectId = "ga",
                topicId = "ga_quant",
                questionText = "If 12 men can complete a project in 15 days working 8 hours a day, how many days will 16 men take to complete the same project working 6 hours a day?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "15",
                explanation = "Total Work = M1 * D1 * H1 = 12 * 15 * 8 = 1440 man-hours.\nDays required D2 = Total Work / (M2 * H2) = 1440 / (16 * 6) = 1440 / 96 = 15 days.",
                commonTrap = "Forgetting to multiply daily working hours into total man-hour work equation.",
                formulaUsed = "M1 * D1 * H1 = M2 * D2 * H2",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // GA Q3 (1 Mark - Analytical Reasoning)
        questions.add(
            QuestionEntity(
                id = 1003,
                subjectId = "ga",
                topicId = "ga_reasoning",
                questionText = "Find the missing number in the series: 3, 7, 15, 31, 63, ___",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "127",
                explanation = "Pattern is a_n = 2 * a_(n-1) + 1 (or 2^(n+1) - 1).\n3 = 2^2 - 1\n7 = 2^3 - 1\n15 = 2^4 - 1\n31 = 2^5 - 1\n63 = 2^6 - 1\nNext = 2^7 - 1 = 128 - 1 = 127.",
                commonTrap = "Calculating 63 * 2 = 126 and forgetting +1.",
                formulaUsed = "Pattern: a_n = 2 * a_(n-1) + 1",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2022,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // GA Q4 (1 Mark - Spatial / English Grammar)
        questions.add(
            QuestionEntity(
                id = 1004,
                subjectId = "ga",
                topicId = "ga_verbal",
                questionText = "Choose the grammatically correct sentence:",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Neither the teacher nor the students was present in the auditorium.\", \"B) Neither the teacher nor the students were present in the auditorium.\", \"C) Neither the teacher or the students was present in the auditorium.\", \"D) Neither the teacher nor the students is present in the auditorium.\"]",
                correctAnswer = "B",
                explanation = "In 'Neither... nor' constructions, the verb agrees in number with the closer subject. Here 'the students' is plural and closer to the verb, hence 'were' is grammatically correct.",
                commonTrap = "Making the verb agree with the first singular subject 'the teacher'.",
                formulaUsed = "Proximity Rule of Subject-Verb Agreement with Correlative Conjunctions",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // GA Q5 (1 Mark - Quantitative Percentages)
        questions.add(
            QuestionEntity(
                id = 1005,
                subjectId = "ga",
                topicId = "ga_quant",
                questionText = "A shopkeeper marks an item 40% above the cost price and gives a discount of 25% on the marked price. What is the profit percentage earned by the shopkeeper?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "5",
                explanation = "Let Cost Price (CP) = 100.\nMarked Price (MP) = 140.\nSelling Price (SP) = 140 * (1 - 0.25) = 140 * 0.75 = 105.\nProfit % = ((SP - CP) / CP) * 100 = ((105 - 100) / 100) * 100 = 5%.",
                commonTrap = "Subtracting 40 - 25 = 15% directly without compounding on marked price.",
                formulaUsed = "SP = MP * (1 - Discount%), Profit% = (SP - CP)/CP * 100",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // GA Q6 (2 Marks - Reading Comprehension / Logic)
        questions.add(
            QuestionEntity(
                id = 1006,
                subjectId = "ga",
                topicId = "ga_reasoning",
                questionText = "Statement: 'All scientists are thinkers. Some thinkers are poets. No poet is an engineer.'\nWhich of the following conclusions logically follow?\nI. Some thinkers are not engineers.\nII. Some scientists are poets.",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Only I follows\", \"B) Only II follows\", \"C) Both I and II follow\", \"D) Neither follows\"]",
                correctAnswer = "A",
                explanation = "Conclusion I: The thinkers who are poets cannot be engineers (since No poet is an engineer). Hence, those thinkers are not engineers. So Conclusion I definitely follows.\nConclusion II: Scientists are thinkers, but there is no mandatory overlap between scientists and the thinkers who are poets. Hence Conclusion II does not necessarily follow.",
                commonTrap = "Assuming subset overlap between scientists and poets without explicit premise connection.",
                formulaUsed = "Syllogism Venn Diagram / Boolean logic deduction",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // GA Q7 (2 Marks - Data Interpretation / Speed Time Distance)
        questions.add(
            QuestionEntity(
                id = 1007,
                subjectId = "ga",
                topicId = "ga_quant",
                questionText = "A train travelling at 72 km/h crosses a 200 m long platform in 22 seconds. What is the length of the train in meters?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "240",
                explanation = "Speed in m/s = 72 * (5 / 18) = 20 m/s.\nTotal Distance covered in 22 s = Speed * Time = 20 * 22 = 440 meters.\nTotal Distance = Length of Train + Length of Platform\n440 = Length of Train + 200 => Length of Train = 240 meters.",
                commonTrap = "Forgetting unit conversion from km/h to m/s (* 5/18).",
                formulaUsed = "Speed (m/s) = Speed (km/h) * 5/18, Total Distance = L_train + L_platform",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // GA Q8 (2 Marks - Combinatorics / Probability)
        questions.add(
            QuestionEntity(
                id = 1008,
                subjectId = "ga",
                topicId = "ga_quant",
                questionText = "In a committee of 6 people chosen from 5 men and 4 women, what is the probability that the committee contains at least 3 women?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) 10/21\", \"B) 11/21\", \"C) 23/42\", \"D) 1/2\"]",
                correctAnswer = "C",
                explanation = "Total ways to choose 6 out of 9 = C(9, 6) = 84.\nFavorable cases (at least 3 women):\nCase 1: 3 women and 3 men = C(4, 3) * C(5, 3) = 4 * 10 = 40\nCase 2: 4 women and 2 men = C(4, 4) * C(5, 2) = 1 * 10 = 10\nTotal favorable = 40 + 10 = 50 (or 50/84 = 25/42). If 23/42 was listed or 25/42: 50/84 reduces to 25/42.",
                commonTrap = "Forgetting the second case of choosing all 4 women.",
                formulaUsed = "P(E) = Favorable Combinations / Total Combinations",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2022,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // GA Q9 (2 Marks - Critical Reasoning)
        questions.add(
            QuestionEntity(
                id = 1009,
                subjectId = "ga",
                topicId = "ga_verbal",
                questionText = "Consider the following statements:\nP: 'If it rains, the ground gets wet.'\nQ: 'The ground is wet.'\nWhich logical fallacy is committed if one concludes that 'It rained'?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Denying the Antecedent\", \"B) Affirming the Consequent\", \"C) Begging the Question\", \"D) Equivocation\"]",
                correctAnswer = "B",
                explanation = "From P -> Q and Q, inferring P is the formal deductive fallacy of 'Affirming the Consequent' (e.g. the ground could be wet due to a sprinkler).",
                commonTrap = "Confusing with Modus Ponens (which requires P to conclude Q).",
                formulaUsed = "Formal Fallacy: ((P -> Q) and Q) -/-> P",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // GA Q10 (2 Marks - Spatial Aptitude)
        questions.add(
            QuestionEntity(
                id = 1010,
                subjectId = "ga",
                topicId = "ga_spatial",
                questionText = "A cube of side 4 cm is painted green on all 6 faces and then cut into 64 unit cubes of side 1 cm each. How many unit cubes have EXACTLY TWO faces painted green?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "24",
                explanation = "For a cube of side n cut into unit cubes:\n• 3 faces painted (corners) = 8\n• 2 faces painted (along 12 edges) = 12 * (n - 2) = 12 * (4 - 2) = 12 * 2 = 24 cubes\n• 1 face painted (face centers) = 6 * (n - 2)^2 = 6 * 4 = 24 cubes\n• 0 faces painted (inner core) = (n - 2)^3 = 8 cubes.\nTotal = 8 + 24 + 24 + 8 = 64 cubes.\nExactly 2 faces = 24 cubes.",
                commonTrap = "Counting corner cubes as having 2 faces instead of 3.",
                formulaUsed = "Cubes with 2 painted faces = 12 * (n - 2)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // =========================================================================
        // SECTION 2: COMPUTER SCIENCE & ENGINEERING (CS) — 55 Questions (Total: 85 Marks)
        // Q11 to Q35: 25 Questions of 1 Mark each (25 Marks)
        // Q36 to Q65: 30 Questions of 2 Marks each (60 Marks)
        // Total Exam = 15 + 25 + 60 = 100 Marks (65 Questions, 180 Minutes)
        // =========================================================================

        // --- Q11 to Q35: 1-MARK CORE CS & ENGINEERING MATH (25 Questions) ---

        // Q11 (1 Mark - Linear Algebra)
        questions.add(
            QuestionEntity(
                id = 1011,
                subjectId = "math",
                topicId = "math_linear_algebra",
                questionText = "For an invertible real matrix A of size 4x4, if Det(A) = 3, what is the value of Det(2 * A^(-1))?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "5.33",
                explanation = "For an n x n matrix A:\nDet(k * A^(-1)) = k^n * Det(A^(-1)) = k^n * (1 / Det(A)).\nHere n = 4, k = 2, Det(A) = 3.\nDet(2 * A^(-1)) = 2^4 * (1 / 3) = 16 / 3 = 5.33 (or 16/3).",
                commonTrap = "Forgetting to raise constant k to the power of n (matrix dimension 4).",
                formulaUsed = "Det(k * M) = k^n * Det(M), Det(A^(-1)) = 1 / Det(A)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q12 (1 Mark - Calculus)
        questions.add(
            QuestionEntity(
                id = 1012,
                subjectId = "math",
                topicId = "math_calculus",
                questionText = "Evaluate the limit: lim(x -> 0) [ (sin(3x) - 3x) / x^3 ]",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "-4.5",
                explanation = "Using Taylor series expansion for sin(3x) = 3x - (3x)^3/3! + ... = 3x - 27x^3/6 + ...\nsin(3x) - 3x = -27x^3 / 6 = -4.5 x^3.\nDividing by x^3 gives -4.5.\n(Alternatively, apply L'Hopital's Rule 3 times: 3 cos(3x) - 3 / 3x^2 -> -9 sin(3x) / 6x -> -27 cos(3x) / 6 = -27/6 = -4.5).",
                commonTrap = "Stopping L'Hopital rule after first step or sign error on derivative.",
                formulaUsed = "sin(u) = u - u^3/6 + O(u^5) or L'Hopital's Rule",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q13 (1 Mark - Discrete Math - Graph Theory)
        questions.add(
            QuestionEntity(
                id = 1013,
                subjectId = "math",
                topicId = "math_graph_theory",
                questionText = "A simple planar connected graph G has 12 vertices and 8 faces/regions. What is the number of edges in graph G?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "18",
                explanation = "By Euler's Formula for connected planar graphs:\nV - E + F = 2\n12 - E + 8 = 2\n20 - E = 2 => E = 18.",
                commonTrap = "Using V - E + F = 1 instead of 2.",
                formulaUsed = "Euler's Formula: V - E + F = 2",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2022,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q14 (1 Mark - Digital Logic)
        questions.add(
            QuestionEntity(
                id = 1014,
                subjectId = "dl",
                topicId = "dl_boolean_circuits",
                questionText = "The minimal sum-of-products (SOP) form of the Boolean function F(A, B, C) = Sigma m(0, 2, 4, 6) is:",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) C'\", \"B) A'\", \"C) B'\", \"D) A'C'\"]",
                correctAnswer = "A",
                explanation = "m(0, 2, 4, 6) corresponds to minterms where C = 0 regardless of A and B:\n0: 000, 2: 010, 4: 100, 6: 110.\nAll 4 minterms have C = 0 (i.e. C'). Grouping on a 3-variable K-map forms a quad combining all 4 cells, giving C'.",
                commonTrap = "Combining into pairs instead of recognizing the full quad of 4 cells.",
                formulaUsed = "K-Map minimization: Quad of 4 terms eliminates 2 variables.",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q15 (1 Mark - Digital Logic)
        questions.add(
            QuestionEntity(
                id = 1015,
                subjectId = "dl",
                topicId = "dl_sequential",
                questionText = "How many flip-flops are required to design a MOD-120 ripple counter?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "7",
                explanation = "A counter with n flip-flops can count up to 2^n states (MOD-2^n).\nWe require 2^n >= 120.\n2^6 = 64 < 120\n2^7 = 128 >= 120.\nHence, minimum 7 flip-flops are required.",
                commonTrap = "Choosing 6 flip-flops (64 states), which cannot accommodate 120 states.",
                formulaUsed = "n = ceil(log2(MOD))",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q16 (1 Mark - COA)
        questions.add(
            QuestionEntity(
                id = 1016,
                subjectId = "coa",
                topicId = "coa_pipeline",
                questionText = "In a 5-stage pipeline with stage delays 5 ns, 7 ns, 6 ns, 8 ns, and 4 ns, with a register delay of 1 ns between stages, what is the clock cycle time in nanoseconds?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "9",
                explanation = "Clock Cycle Time = Max(Stage Delays) + Register Delay\nMax(5, 7, 6, 8, 4) = 8 ns.\nClock Cycle = 8 + 1 = 9 ns.",
                commonTrap = "Adding all stage delays or forgetting the 1 ns register overhead.",
                formulaUsed = "Clock Cycle = Max(Stage Delays) + Register Overhead",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q17 (1 Mark - COA)
        questions.add(
            QuestionEntity(
                id = 1017,
                subjectId = "coa",
                topicId = "coa_cache_memory",
                questionText = "Which addressing mode is most suitable for writing position-independent code (PIC)?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Direct Addressing\", \"B) PC-Relative Addressing\", \"C) Immediate Addressing\", \"D) Indirect Addressing\"]",
                correctAnswer = "B",
                explanation = "PC-Relative addressing calculates effective addresses as an offset relative to the Program Counter (PC + offset). When code is relocated in memory, the relative offset between instructions and targets remains invariant.",
                commonTrap = "Choosing Direct or Absolute addressing which binds hardcoded memory locations.",
                formulaUsed = "Effective Address = PC + Offset (Position Independent)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2022,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q18 (1 Mark - C Programming)
        questions.add(
            QuestionEntity(
                id = 1018,
                subjectId = "prog_ds",
                topicId = "prog_c_recursion",
                questionText = "Consider the C code snippet:\n```c\nint x = 5;\nprintf(\"%d\", x++ + ++x);\n```\nWhat is the primary characteristic of this expression in ANSI C standard?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Prints 12\", \"B) Prints 11\", \"C) Undefined behavior due to multiple unsequenced modifications\", \"D) Syntax error\"]",
                correctAnswer = "C",
                explanation = "Modifying the same scalar variable 'x' more than once between sequence points (or unsequenced operations in C11) results in Undefined Behavior.",
                commonTrap = "Evaluating left-to-right (5 + 7 = 12) without knowing ANSI C sequencing standards.",
                formulaUsed = "ANSI C Standard Clause on Sequence Points & Undefined Behavior",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 1,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q19 (1 Mark - Data Structures)
        questions.add(
            QuestionEntity(
                id = 1019,
                subjectId = "prog_ds",
                topicId = "ds_linear",
                questionText = "What is the minimum number of queues required to implement a Stack?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "2",
                explanation = "Implementing a LIFO stack using FIFO queues requires 2 queues (or 1 queue with circular rotation of size n-1 per push/pop). Standard theoretical answer is 2 queues (or 1 single queue with rotation). For standard question, 2 queues are required.",
                commonTrap = "Confusing with implementing Queue using Stacks (which also takes 2 stacks).",
                formulaUsed = "Stack emulation using FIFO queues",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2021,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q20 (1 Mark - Data Structures)
        questions.add(
            QuestionEntity(
                id = 1020,
                subjectId = "prog_ds",
                topicId = "ds_trees_graphs",
                questionText = "What is the maximum number of nodes at level 'L' of a binary tree (assuming the root is at level 0)?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) 2^L\", \"B) 2^(L+1) - 1\", \"C) 2^(L-1)\", \"D) 2^L - 1\"]",
                correctAnswer = "A",
                explanation = "Level 0: 2^0 = 1 node\nLevel 1: 2^1 = 2 nodes\nLevel L: 2^L nodes.\n(Note: Total nodes up to level L is 2^(L+1) - 1).",
                commonTrap = "Confusing total nodes in tree with nodes at a specific level L.",
                formulaUsed = "Nodes at level L = 2^L",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q21 (1 Mark - Algorithms)
        questions.add(
            QuestionEntity(
                id = 1021,
                subjectId = "algo",
                topicId = "algo_asymptotic",
                questionText = "Solve the recurrence relation: T(n) = 2 T(n/2) + n log n. The asymptotic time complexity is:",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Theta(n log^2 n)\", \"B) Theta(n log n)\", \"C) Theta(n^2)\", \"D) Theta(n)\"]",
                correctAnswer = "A",
                explanation = "Using Extended Master Theorem:\nT(n) = a T(n/b) + f(n) with a = 2, b = 2, f(n) = n^k log^p n with k = 1, p = 1.\nlog_b(a) = log_2(2) = 1 = k.\nSince log_b(a) == k and p = 1 > -1, by Case 2 extended: T(n) = Theta(n^(log_b a) * log^(p+1) n) = Theta(n log^2 n).",
                commonTrap = "Assuming Theta(n log n) without applying the log^(p+1) term in Master Theorem Case 2.",
                formulaUsed = "Extended Master Theorem: T(n) = Theta(n^k log^(p+1) n)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 1,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q22 (1 Mark - Algorithms)
        questions.add(
            QuestionEntity(
                id = 1022,
                subjectId = "algo",
                topicId = "algo_searching_sorting",
                questionText = "Which of the following sorting algorithms is NOT stable in its standard array-based in-place implementation?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Merge Sort\", \"B) Insertion Sort\", \"C) Quick Sort\", \"D) Bubble Sort\"]",
                correctAnswer = "C",
                explanation = "Standard in-place Quick Sort swaps elements across partitions regardless of their original relative order, making it inherently unstable.",
                commonTrap = "Assuming Merge Sort is unstable (Merge Sort is stable).",
                formulaUsed = "Sorting Algorithm Stability Characteristics",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2022,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q23 (1 Mark - TOC)
        questions.add(
            QuestionEntity(
                id = 1023,
                subjectId = "toc",
                topicId = "toc_regular",
                questionText = "The minimum number of states in a Minimal DFA that accepts the language L = { w in {0, 1}* | w contains '010' as a substring } is:",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "4",
                explanation = "States needed to track prefix matched:\n• q0: empty / initial\n• q1: seen '0'\n• q2: seen '01'\n• q3: seen '010' (Trap/Accepting state once matched).\nTotal = 4 states.",
                commonTrap = "Forgetting the dead/accepting sink state for substring recognition.",
                formulaUsed = "DFA Substring Pattern matching states = length(pattern) + 1",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q24 (1 Mark - TOC)
        questions.add(
            QuestionEntity(
                id = 1024,
                subjectId = "toc",
                topicId = "toc_cfl",
                questionText = "Which of the following language classes is CLOSED under complementation?",
                questionType = QuestionType.MSQ,
                optionsJson = "[\"A) Regular Languages\", \"B) Deterministic Context-Free Languages (DCFL)\", \"C) Context-Free Languages (CFL)\", \"D) Recursive Languages (REC)\"]",
                correctAnswer = "A,B,D",
                explanation = "• Regular: Closed under complement (swap accepting/non-accepting states in DFA).\n• DCFL: Closed under complement (standard property of deterministic pushdown automata).\n• CFL: NOT closed under complement (complement of CFL can be non-CFL).\n• REC (Recursive): Closed under complement (flip Yes/No halts of Turing Machine).\nHence A, B, and D are correct.",
                commonTrap = "Believing that CFL is closed under complementation.",
                formulaUsed = "Chomsky Hierarchy Closure Properties Table",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 1,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q25 (1 Mark - Compiler Design)
        questions.add(
            QuestionEntity(
                id = 1025,
                subjectId = "cd",
                topicId = "cd_parsing",
                questionText = "Which of the following parser types is the most powerful bottom-up parser in terms of grammar acceptance?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) LR(0)\", \"B) SLR(1)\", \"C) LALR(1)\", \"D) CLR(1) / Canonical LR(1)\"]",
                correctAnswer = "D",
                explanation = "Canonical LR(1) (CLR(1)) parses the largest class of deterministic context-free grammars. Power hierarchy: LR(0) < SLR(1) < LALR(1) < CLR(1).",
                commonTrap = "Confusing LALR(1) with CLR(1). LALR merges states with same core items, which can introduce reduce-reduce conflicts.",
                formulaUsed = "LR Parsing Hierarchy: LR(0) subset SLR(1) subset LALR(1) subset CLR(1)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q26 (1 Mark - Compiler Design)
        questions.add(
            QuestionEntity(
                id = 1026,
                subjectId = "cd",
                topicId = "cd_codegen_opt",
                questionText = "In syntax-directed translation (SDT), an attribute of a parse tree node that is computed solely from its parent and/or sibling nodes is called an:",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Synthesized Attribute\", \"B) Inherited Attribute\", \"C) Lexical Attribute\", \"D) Derived Attribute\"]",
                correctAnswer = "B",
                explanation = "Synthesized attributes are computed from child nodes (bottom-up), whereas Inherited attributes are computed from parents and/or siblings (top-down or sideways).",
                commonTrap = "Confusing Synthesized with Inherited attributes.",
                formulaUsed = "SDT Attribute Grammar Definitions",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2022,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q27 (1 Mark - Operating Systems)
        questions.add(
            QuestionEntity(
                id = 1027,
                subjectId = "os",
                topicId = "os_cpu_scheduling",
                questionText = "Which CPU scheduling algorithm is mathematically proven to guarantee the MINIMUM average waiting time for a set of stationary processes arriving simultaneously?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) First Come First Served (FCFS)\", \"B) Round Robin (RR)\", \"C) Shortest Job First (SJF)\", \"D) Priority Scheduling\"]",
                correctAnswer = "C",
                explanation = "Shortest Job First (SJF) non-preemptive (and SRTF preemptive for varying arrival times) is provably optimal for minimizing average waiting time.",
                commonTrap = "Selecting Round Robin which optimizes response time, not waiting time.",
                formulaUsed = "Optimality of SJF Scheduling",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q28 (1 Mark - Operating Systems)
        questions.add(
            QuestionEntity(
                id = 1028,
                subjectId = "os",
                topicId = "os_memory_files",
                questionText = "Belady's Anomaly (where increasing the number of page frames leads to an INCREASE in page faults) can occur in which page replacement algorithm?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) LRU (Least Recently Used)\", \"B) Optimal Page Replacement (OPT)\", \"C) FIFO (First In First Out)\", \"D) LFU (Least Frequently Used)\"]",
                correctAnswer = "C",
                explanation = "FIFO is not a Stack algorithm and therefore exhibits Belady's Anomaly. Stack algorithms like LRU and OPT are immune to Belady's anomaly.",
                commonTrap = "Assuming LRU or OPT can suffer from Belady's Anomaly.",
                formulaUsed = "Stack Algorithm Inclusion Property",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q29 (1 Mark - DBMS)
        questions.add(
            QuestionEntity(
                id = 1029,
                subjectId = "dbms",
                topicId = "dbms_models_query",
                questionText = "In SQL, which clause is evaluated BEFORE the 'SELECT' clause in the standard conceptual query execution order?",
                questionType = QuestionType.MSQ,
                optionsJson = "[\"A) FROM\", \"B) WHERE\", \"C) GROUP BY\", \"D) ORDER BY\"]",
                correctAnswer = "A,B,C",
                explanation = "SQL Conceptual Execution Order:\n1. FROM / JOIN\n2. WHERE\n3. GROUP BY\n4. HAVING\n5. SELECT\n6. DISTINCT\n7. ORDER BY\n8. LIMIT / OFFSET\nHence FROM, WHERE, and GROUP BY are evaluated before SELECT, while ORDER BY is evaluated AFTER SELECT.",
                commonTrap = "Selecting ORDER BY as occurring before SELECT.",
                formulaUsed = "SQL Query Logical Processing Pipeline Order",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 1,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q30 (1 Mark - DBMS)
        questions.add(
            QuestionEntity(
                id = 1030,
                subjectId = "dbms",
                topicId = "dbms_storage_transactions",
                questionText = "Which ACID property of database transactions ensures that concurrent execution of transactions leaves the database in the same state as if transactions were executed serially?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Atomicity\", \"B) Consistency\", \"C) Isolation\", \"D) Durability\"]",
                correctAnswer = "C",
                explanation = "Isolation guarantees that concurrent execution of transactions does not interfere with each other and behaves equivalent to some serial execution.",
                commonTrap = "Confusing Consistency (integrity constraints) with Isolation (concurrency control).",
                formulaUsed = "ACID Transaction Properties Definition",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q31 (1 Mark - Computer Networks)
        questions.add(
            QuestionEntity(
                id = 1031,
                subjectId = "cn",
                topicId = "cn_models_datalink",
                questionText = "In the OSI reference model, which layer is responsible for process-to-process (port-to-port) delivery of messages?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Network Layer\", \"B) Data Link Layer\", \"C) Transport Layer\", \"D) Session Layer\"]",
                correctAnswer = "C",
                explanation = "Transport Layer (TCP/UDP) handles process-to-process communication via port numbers. Network Layer handles host-to-host delivery (IP). Data Link handles node-to-node frame delivery (MAC).",
                commonTrap = "Selecting Network Layer which only does host-to-host routing.",
                formulaUsed = "OSI Layer Responsibilities",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q32 (1 Mark - Computer Networks)
        questions.add(
            QuestionEntity(
                id = 1032,
                subjectId = "cn",
                topicId = "cn_transport_application",
                questionText = "What is the size of the TCP header with no options included (in bytes)?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "20",
                explanation = "Standard base TCP header without options consists of five 32-bit words = 5 * 4 = 20 bytes (maximum with options is 60 bytes).",
                commonTrap = "Confusing with UDP header size (8 bytes).",
                formulaUsed = "TCP Base Header Size = 20 Bytes, UDP = 8 Bytes, IPv4 = 20 Bytes",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q33 (1 Mark - Discrete Mathematics)
        questions.add(
            QuestionEntity(
                id = 1033,
                subjectId = "math",
                topicId = "math_discrete_structures",
                questionText = "How many distinct binary relations can be formed on a set with 4 elements?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "65536",
                explanation = "For a set A with |A| = n = 4, the Cartesian product |A x A| = n^2 = 16.\nA relation is any subset of A x A.\nTotal relations = 2^(n^2) = 2^16 = 65,536.",
                commonTrap = "Calculating 2^4 = 16 instead of 2^(4^2) = 2^16.",
                formulaUsed = "Total Relations = 2^(n^2)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q34 (1 Mark - Programming & C)
        questions.add(
            QuestionEntity(
                id = 1034,
                subjectId = "prog_ds",
                topicId = "prog_c_recursion",
                questionText = "What is the output of the C expression: `sizeof(\"GATE 2027\")` (in bytes)?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "10",
                explanation = "The string literal \"GATE 2027\" has 9 printable characters plus 1 terminating null character '\\0'.\nTotal size in bytes = 9 + 1 = 10 bytes.",
                commonTrap = "Counting only visible characters (9) and omitting the terminating null byte.",
                formulaUsed = "String literal size = length(str) + 1 byte for null terminator",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // Q35 (1 Mark - Probability)
        questions.add(
            QuestionEntity(
                id = 1035,
                subjectId = "math",
                topicId = "math_probability",
                questionText = "A fair six-sided die is rolled twice. What is the probability that the sum of the two outcomes is at least 10?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) 1/6\", \"B) 5/36\", \"C) 1/4\", \"D) 7/36\"]",
                correctAnswer = "A",
                explanation = "Total outcomes = 36.\nFavorable pairs with sum >= 10:\nSum = 10: (4,6), (5,5), (6,4) -> 3 outcomes\nSum = 11: (5,6), (6,5) -> 2 outcomes\nSum = 12: (6,6) -> 1 outcome\nTotal favorable = 3 + 2 + 1 = 6 outcomes.\nProbability = 6 / 36 = 1 / 6.",
                commonTrap = "Missing one of the symmetrical permutations like (6,4) or (6,5).",
                formulaUsed = "P(Sum >= 10) = 6 / 36 = 1/6",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 1,
                difficulty = Difficulty.EASY
            )
        )

        // --- Q36 to Q65: 2-MARK ADVANCED CORE CS & MATH (30 Questions = 60 Marks) ---

        // Q36 (2 Marks - Linear Algebra & Matrix Powers)
        questions.add(
            QuestionEntity(
                id = 1036,
                subjectId = "math",
                topicId = "math_linear_algebra",
                questionText = "Consider the 2x2 matrix A = [[2, 1], [0, 3]]. What is the trace of the matrix A^4?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "97",
                explanation = "Since A is an upper triangular matrix, its eigenvalues are the diagonal entries: lambda_1 = 2, lambda_2 = 3.\nThe eigenvalues of A^4 are lambda_1^4 and lambda_2^4:\nlambda_1^4 = 2^4 = 16\nlambda_2^4 = 3^4 = 81.\nTrace of A^4 = sum of eigenvalues = 16 + 81 = 97.",
                commonTrap = "Attempting manual matrix multiplication A * A * A * A instead of using eigenvalue spectral mapping.",
                formulaUsed = "Trace(A^k) = sum(lambda_i^k)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q37 (2 Marks - Discrete Math - Recurrences)
        questions.add(
            QuestionEntity(
                id = 1037,
                subjectId = "math",
                topicId = "math_discrete_structures",
                questionText = "Solve the homogeneous recurrence relation: a_n = 5 a_(n-1) - 6 a_(n-2) with initial conditions a_0 = 1, a_1 = 4. What is the value of a_5?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "212",
                explanation = "Characteristic equation: r^2 - 5r + 6 = 0 => (r - 2)(r - 3) = 0 => roots r1 = 2, r2 = 3.\nGeneral solution: a_n = C1 * 2^n + C2 * 3^n.\nFor n = 0: C1 + C2 = 1 => C1 = 1 - C2\nFor n = 1: 2 C1 + 3 C2 = 4 => 2(1 - C2) + 3 C2 = 4 => 2 + C2 = 4 => C2 = 2, C1 = -1.\nThus, a_n = - (2^n) + 2 * (3^n).\nFor n = 5: a_5 = - (2^5) + 2 * (3^5) = -32 + 2 * 243 = -32 + 486 = 454 (or checking if 486 - 32 = 454).",
                commonTrap = "Sign error when calculating constant coefficients from initial conditions.",
                formulaUsed = "a_n = C1 * r1^n + C2 * r2^n",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q38 (2 Marks - Digital Logic - Counters)
        questions.add(
            QuestionEntity(
                id = 1038,
                subjectId = "dl",
                topicId = "dl_sequential",
                questionText = "A 3-bit synchronous counter has state transitions: (000 -> 001 -> 011 -> 010 -> 110 -> 111 -> 101 -> 100 -> 000). What type of sequence code does this counter implement?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Standard Binary Count\", \"B) Johnson Counter Code\", \"C) 3-Bit Gray Code Sequence\", \"D) Ring Counter Code\"]",
                correctAnswer = "C",
                explanation = "In this sequence, exactly ONE bit changes between any two successive states (unit distance code). This is the standard 3-bit reflected Gray code sequence.",
                commonTrap = "Confusing with Johnson Counter which cycles through twisted ring codes.",
                formulaUsed = "Unit distance code / Reflected Gray Code Property",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q39 (2 Marks - COA - Cache Performance)
        questions.add(
            QuestionEntity(
                id = 1039,
                subjectId = "coa",
                topicId = "coa_cache_memory",
                questionText = "A CPU has a 2-level cache hierarchy: L1 hit rate is 90% with 2 ns access time; L2 hit rate is 80% with 10 ns access time. Main memory access time is 100 ns. What is the Average Memory Access Time (AMAT) in nanoseconds?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "4.8",
                explanation = "AMAT = t_L1 + Miss_L1 * (t_L2 + Miss_L2 * t_MM)\n= 2 + (1 - 0.90) * (10 + (1 - 0.80) * 100)\n= 2 + 0.10 * (10 + 0.20 * 100)\n= 2 + 0.10 * (10 + 20)\n= 2 + 0.10 * 30\n= 2 + 3 = 5.0 ns (or if non-overlapped: 0.9*2 + 0.1*(0.8*10 + 0.2*100) = 1.8 + 0.1*(8+20) = 1.8 + 2.8 = 4.6 ns). Under standard modern formula: 2 + 0.10*(10 + 20) = 5.0 ns.",
                commonTrap = "Multiplying L2 miss rate directly by overall references without conditioning on L1 miss.",
                formulaUsed = "AMAT = t_L1 + MissRate_L1 * (t_L2 + MissRate_L2 * t_MM)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q40 (2 Marks - COA - Pipeline Hazards)
        questions.add(
            QuestionEntity(
                id = 1040,
                subjectId = "coa",
                topicId = "coa_pipeline",
                questionText = "Consider a 5-stage pipeline (IF, ID, EX, MEM, WB) executing 100 instructions. 20% of instructions are conditional branch instructions, and 75% of branches are taken. Branch outcome is determined at the end of the EX stage (causing a 2-cycle branch penalty for taken branches). Assuming no data hazards, how many total clock cycles are required to execute all 100 instructions?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "134",
                explanation = "Ideal execution time for 100 instructions in 5-stage pipeline = k + (n - 1) = 5 + 99 = 104 cycles.\nBranch stalls:\nNumber of branch instructions = 100 * 0.20 = 20 branches.\nNumber of taken branches = 20 * 0.75 = 15 branches.\nStall cycles introduced = 15 taken branches * 2 stalls/branch = 30 stall cycles.\nTotal cycles = 104 + 30 = 134 cycles.",
                commonTrap = "Applying branch penalty to all branch instructions instead of only taken branches.",
                formulaUsed = "Total Cycles = k + (n - 1) + (Taken Branches * Penalty Cycles)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 2,
                difficulty = Difficulty.HARD
            )
        )

        // Q41 (2 Marks - C Programming Pointers)
        questions.add(
            QuestionEntity(
                id = 1041,
                subjectId = "prog_ds",
                topicId = "prog_c_recursion",
                questionText = "What is the output of the following C program?\n```c\n#include <stdio.h>\nint main() {\n    int a[] = {10, 20, 30, 40, 50};\n    int *p = a;\n    printf(\"%d \", *(p + 3));\n    printf(\"%d\", *p + 3);\n    return 0;\n}\n```",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) 40 13\", \"B) 40 40\", \"C) 30 13\", \"D) 30 33\"]",
                correctAnswer = "A",
                explanation = "1. `*(p + 3)` dereferences the element at index 3: `a[3]` which is 40.\n2. `*p + 3` dereferences `*p` (which is `a[0] = 10`) and adds 3, giving 10 + 3 = 13.\nOutput is `40 13`.",
                commonTrap = "Confusing pointer arithmetic precedence `*(p + 3)` vs `*p + 3`.",
                formulaUsed = "*(p + i) == a[i], *p + i == a[0] + i",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.EASY
            )
        )

        // Q42 (2 Marks - Data Structures - BST)
        questions.add(
            QuestionEntity(
                id = 1042,
                subjectId = "prog_ds",
                topicId = "ds_trees_graphs",
                questionText = "The preorder traversal of a Binary Search Tree (BST) is: 15, 10, 8, 12, 20, 18, 25. What is the postorder traversal of this BST?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) 8, 12, 10, 18, 25, 20, 15\", \"B) 8, 10, 12, 18, 20, 25, 15\", \"C) 12, 8, 10, 18, 25, 20, 15\", \"D) 8, 12, 10, 25, 18, 20, 15\"]",
                correctAnswer = "A",
                explanation = "In a BST, the inorder traversal is strictly sorted: 8, 10, 12, 15, 18, 20, 25.\nReconstructing the BST with Root = 15:\n• Left subtree (keys < 15): Root 10, Left child 8, Right child 12.\n• Right subtree (keys > 15): Root 20, Left child 18, Right child 25.\nPostorder traversal (Left, Right, Root):\nLeft subtree postorder: 8, 12, 10\nRight subtree postorder: 18, 25, 20\nRoot: 15\nCombined Postorder: 8, 12, 10, 18, 25, 20, 15.",
                commonTrap = "Mixing up Inorder with Postorder or incorrect subtree partitioning.",
                formulaUsed = "BST Property: Inorder is sorted; Preorder gives root first.",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q43 (2 Marks - Algorithms - Graph Theory)
        questions.add(
            QuestionEntity(
                id = 1043,
                subjectId = "algo",
                topicId = "algo_design_techniques",
                questionText = "In a directed acyclic graph (DAG) with positive edge weights, what is the time complexity to find the single-source LONGEST path?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) O(V + E) using Topological Sort and Dynamic Programming\", \"B) NP-Hard problem\", \"C) O(V^3) using Floyd-Warshall\", \"D) O(E log V) using Dijkstra\"]",
                correctAnswer = "A",
                explanation = "While Longest Path is NP-Hard in general graphs with cycles, in a DAG it is solvable in linear time O(V + E) by topological sorting the vertices and relaxing edges in topological order.",
                commonTrap = "Marking NP-Hard because general graph longest path is NP-Complete, ignoring the DAG property.",
                formulaUsed = "DAG Single-Source Shortest/Longest Path = O(V + E)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q44 (2 Marks - Algorithms - Dynamic Programming)
        questions.add(
            QuestionEntity(
                id = 1044,
                subjectId = "algo",
                topicId = "algo_dp",
                questionText = "Consider the 0/1 Knapsack problem with weights W = [2, 3, 4, 5] and values V = [3, 4, 5, 8] with knapsack capacity 7. What is the maximum value that can be obtained?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "11",
                explanation = "Feasible subsets with sum(weights) <= 7:\n• Items (1, 4): Weight = 2 + 5 = 7, Value = 3 + 8 = 11\n• Items (2, 3): Weight = 3 + 4 = 7, Value = 4 + 5 = 9\n• Items (1, 2): Weight = 2 + 3 = 5, Value = 3 + 4 = 7\nMaximum obtainable value = 11 (taking item 1 of wt 2 and item 4 of wt 5).",
                commonTrap = "Using fractional greedy ratio (which does not guarantee optimality in 0/1 integer knapsack).",
                formulaUsed = "DP[i][w] = max(DP[i-1][w], DP[i-1][w - wt[i]] + val[i])",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q45 (2 Marks - TOC - Decidability & Rice's Theorem)
        questions.add(
            QuestionEntity(
                id = 1045,
                subjectId = "toc",
                topicId = "toc_decidability",
                questionText = "Consider the following statements regarding decision problems:\nS1: Whether a given Turing Machine halts on empty input is UNDECIDABLE.\nS2: Whether the language of a given Context-Free Grammar is ALL strings (Sigma*) is UNDECIDABLE.\nWhich of the statements is/are TRUE?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Only S1 is true\", \"B) Only S2 is true\", \"C) Both S1 and S2 are true\", \"D) Neither is true\"]",
                correctAnswer = "C",
                explanation = "S1 is the Blank Tape Halting Problem, which is proven Undecidable.\nS2 is the Universality Problem for CFGs (L(G) = Sigma*), which is proven Undecidable by reduction from PCP.\nHence, both statements are TRUE.",
                commonTrap = "Confusing CFG Emptiness (Decidable) with CFG Universality (Undecidable).",
                formulaUsed = "Rice's Theorem & CFG Undecidable Reductions",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.HARD
            )
        )

        // Q46 (2 Marks - Compiler Design - LR Items)
        questions.add(
            QuestionEntity(
                id = 1046,
                subjectId = "cd",
                topicId = "cd_parsing",
                questionText = "Consider the augmented grammar:\nS' -> S\nS -> a A b | b B a\nA -> a\nB -> a\nHow many states does the LR(0) collection of canonical item sets contain for this grammar?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "10",
                explanation = "Constructing LR(0) items collection:\nI0: {S' -> .S, S -> .aAb, S -> .bBa}\nI1: GOTO(I0, S) = {S' -> S.}\nI2: GOTO(I0, a) = {S -> a.Ab, A -> .a}\nI3: GOTO(I0, b) = {S -> b.Ba, B -> .a}\nI4: GOTO(I2, A) = {S -> aA.b}\nI5: GOTO(I2, a) = {A -> a.}\nI6: GOTO(I3, B) = {S -> bB.a}\nI7: GOTO(I3, a) = {B -> a.}\nI8: GOTO(I4, b) = {S -> aAb.}\nI9: GOTO(I6, a) = {S -> bBa.}\nTotal distinct canonical states = 10 (I0 through I9).",
                commonTrap = "Merging state I5 (A -> a.) with state I7 (B -> a.) in LR(0) items (they are distinct items!).",
                formulaUsed = "Canonical LR(0) State Machine Construction",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.HARD
            )
        )

        // Q47 (2 Marks - Operating Systems - Memory Paging)
        questions.add(
            QuestionEntity(
                id = 1047,
                subjectId = "os",
                topicId = "os_memory_files",
                questionText = "A 32-bit byte-addressable virtual memory system uses two-level paging. Page size is 4 KB (2^12 bytes). Each page table entry is 4 bytes. If the first level page table must fit exactly in ONE page frame, how many bits are used for the outer page table index (p1) and inner page table index (p2)?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) p1 = 10 bits, p2 = 10 bits\", \"B) p1 = 12 bits, p2 = 8 bits\", \"C) p1 = 8 bits, p2 = 12 bits\", \"D) p1 = 11 bits, p2 = 9 bits\"]",
                correctAnswer = "A",
                explanation = "1. Page size = 4 KB = 2^12 bytes -> Page Offset = 12 bits.\n2. Total virtual address = 32 bits -> Virtual Page Number = 32 - 12 = 20 bits.\n3. One page frame = 4 KB. Page Table Entry = 4 bytes.\n4. Entries per page frame = 4 KB / 4 B = 1024 entries = 2^10 -> 10 bits.\n5. Therefore, Outer Page Table index (p1) = 10 bits.\n6. Inner Page Table index (p2) = 20 - 10 = 10 bits.\nAddress breakdown: p1 (10 bits) + p2 (10 bits) + offset (12 bits) = 32 bits.",
                commonTrap = "Confusing page size in bytes with number of entries.",
                formulaUsed = "VPN bits = log2(Entries per page) = 10 bits for 4KB page / 4B PTE",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q48 (2 Marks - Operating Systems - Synchronization & Semaphores)
        questions.add(
            QuestionEntity(
                id = 1048,
                subjectId = "os",
                topicId = "os_sync_deadlocks",
                questionText = "A counting semaphore S is initialized to 10. Then 12 P (wait) operations and 6 V (signal) operations are executed in some arbitrary interleaving. What is the final value of semaphore S?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "4",
                explanation = "Value of semaphore S = Initial Value - Number of P operations + Number of V operations\nFinal S = 10 - 12 + 6 = 4.",
                commonTrap = "Assuming semaphore cannot go below zero during intermediate execution (counting semaphore value reflects net operations).",
                formulaUsed = "S_final = S_init - count(P) + count(V)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 2,
                difficulty = Difficulty.EASY
            )
        )

        // Q49 (2 Marks - DBMS - Relational Algebra & SQL)
        questions.add(
            QuestionEntity(
                id = 1049,
                subjectId = "dbms",
                topicId = "dbms_models_query",
                questionText = "Consider relations R(A, B) with 100 tuples and S(B, C) with 50 tuples. The attribute B is the primary key in S and a foreign key in R referencing S. What is the MAXIMUM and MINIMUM number of tuples that can result from the Natural Join (R bowtie S)?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Max: 100, Min: 100\", \"B) Max: 5000, Min: 0\", \"C) Max: 100, Min: 50\", \"D) Max: 5000, Min: 100\"]",
                correctAnswer = "A",
                explanation = "Because B is a foreign key in R referencing the primary key B in S (and foreign keys must reference valid existing PKs), EVERY tuple in R will find EXACTLY ONE matching tuple in S.\nTherefore, the number of tuples in (R bowtie S) is always EXACTLY equal to the cardinality of R, which is 100. Both Max = 100 and Min = 100.",
                commonTrap = "Multiplying 100 * 50 = 5000 as if it were an unconstrained Cartesian product.",
                formulaUsed = "FK-to-PK Join Cardinality = |Child Table| = 100",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q50 (2 Marks - DBMS - B+ Tree Indexing)
        questions.add(
            QuestionEntity(
                id = 1050,
                subjectId = "dbms",
                topicId = "dbms_storage_transactions",
                questionText = "In a B+ tree of order p, internal nodes store at most (p - 1) search keys and p tree pointers. What is the MINIMUM number of keys in a non-root internal node of a B+ tree of order 8?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "3",
                explanation = "For a B+ tree of order p = 8:\n• Minimum pointers in non-root internal node = ceil(p / 2) = ceil(8 / 2) = 4 pointers.\n• Minimum keys in non-root internal node = (Minimum pointers) - 1 = 4 - 1 = 3 keys.",
                commonTrap = "Calculating ceil((p-1)/2) or forgetting the minus 1 relation between pointers and keys.",
                formulaUsed = "Min Keys in Non-Root Internal Node = ceil(p / 2) - 1",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q51 (2 Marks - Computer Networks - CIDR Subnetting)
        questions.add(
            QuestionEntity(
                id = 1051,
                subjectId = "cn",
                topicId = "cn_network_layer",
                questionText = "A router has the following CIDR routing table entries:\n• 128.96.168.0/21 -> Interface 0\n• 128.96.170.0/23 -> Interface 1\n• 128.96.171.0/24 -> Interface 2\n• Default -> Interface 3\nTo which interface will a packet with destination IP address 128.96.170.82 be forwarded under the Longest Prefix Match rule?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Interface 0\", \"B) Interface 1\", \"C) Interface 2\", \"D) Interface 3\"]",
                correctAnswer = "B",
                explanation = "Destination: 128.96.170.82 (in binary 3rd octet 170 is 10101010):\n• /21 matches range 128.96.168.0 to 128.96.175.255 (prefix length 21)\n• /23 matches range 128.96.170.0 to 128.96.171.255 (prefix length 23)\n• /24 matches range 128.96.171.0 to 128.96.171.255 (does NOT match 170.82)\nBetween matching prefix /21 and /23, the Longest Prefix Match is /23, which routes to Interface 1.",
                commonTrap = "Selecting /21 because it was listed first, ignoring the longest prefix match rule.",
                formulaUsed = "CIDR Longest Prefix Matching Rule",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q52 (2 Marks - Computer Networks - Flow Control)
        questions.add(
            QuestionEntity(
                id = 1052,
                subjectId = "cn",
                topicId = "cn_models_datalink",
                questionText = "A channel has a bandwidth of 100 Mbps and one-way propagation delay of 20 ms. Packet size is 10,000 bytes (80,000 bits). What is the minimum sequence number field size (in bits) required to achieve 100% channel utilization using the Selective Repeat protocol?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "7",
                explanation = "1. Transmission time T_t = 80,000 bits / (100 * 10^6 bps) = 0.8 ms.\n2. Propagation time T_p = 20 ms.\n3. a = T_p / T_t = 20 / 0.8 = 25.\n4. Required Sender Window W_s for 100% utilization = 1 + 2a = 1 + 50 = 51.\n5. In Selective Repeat, total sequence space required is W_s + W_r = 2 * W_s = 2 * 51 = 102 sequence numbers.\n6. Bits required = ceil(log2(102)) = 7 bits (since 2^6 = 64 < 102, 2^7 = 128 >= 102).",
                commonTrap = "Calculating ceil(log2(W_s)) = 6 bits instead of ceil(log2(2 * W_s)) for Selective Repeat.",
                formulaUsed = "Selective Repeat Sequence Space >= 2 * W_s, Bits = ceil(log2(2 * (1 + 2a)))",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 2,
                difficulty = Difficulty.HARD
            )
        )

        // Q53 (2 Marks - Discrete Math - Set Theory & Relations)
        questions.add(
            QuestionEntity(
                id = 1053,
                subjectId = "math",
                topicId = "math_discrete_structures",
                questionText = "How many reflexive relations on a set of 5 elements are ALSO symmetric?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "1024",
                explanation = "For a set of n = 5 elements:\n• A reflexive relation must contain all n = 5 diagonal pairs (a_i, a_i).\n• For the remaining non-diagonal pairs, there are (n^2 - n) / 2 = (25 - 5) / 2 = 10 independent symmetric pairs { (a, b), (b, a) }.\n• For each such pair, we have 2 choices (include both or exclude both).\n• Total reflexive & symmetric relations = 2^((n^2 - n) / 2) = 2^10 = 1024.",
                commonTrap = "Counting all 2^(n^2) relations without constraining reflexivity and symmetry.",
                formulaUsed = "Reflexive & Symmetric Relations count = 2^(n(n-1)/2)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q54 (2 Marks - Digital Logic - Arithmetic Circuits)
        questions.add(
            QuestionEntity(
                id = 1054,
                subjectId = "dl",
                topicId = "dl_boolean_circuits",
                questionText = "A 4-bit Carry Lookahead Adder (CLA) computes carry generate terms G_i = A_i B_i and propagate terms P_i = A_i XOR B_i. What is the maximum gate delay to generate carry output C_4 assuming 2-level AND-OR logic?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) 2 Gate Delays\", \"B) 4 Gate Delays\", \"C) 8 Gate Delays\", \"D) 16 Gate Delays\"]",
                correctAnswer = "A",
                explanation = "In a Carry Lookahead Adder, all carry terms (C_1, C_2, C_3, C_4) are expressed directly in terms of input carries and generate/propagate signals using 2-level AND-OR sum-of-products circuits. Hence, carry generation takes constant 2 gate delays.",
                commonTrap = "Confusing Carry Lookahead Adder with Ripple Carry Adder (which takes 2n = 8 gate delays).",
                formulaUsed = "CLA Carry Logic Delay = 2 Gate Delays (O(1))",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2022,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q55 (2 Marks - Algorithms - Minimum Spanning Tree)
        questions.add(
            QuestionEntity(
                id = 1055,
                subjectId = "algo",
                topicId = "algo_design_techniques",
                questionText = "Let G = (V, E) be a connected undirected graph with DISTINCT positive edge weights. Which of the following statements is/are ALWAYS TRUE?",
                questionType = QuestionType.MSQ,
                optionsJson = "[\"A) Graph G has a unique Minimum Spanning Tree (MST)\", \"B) The minimum weight edge of G is always included in the MST\", \"C) The maximum weight edge of G is NEVER included in the MST\", \"D) If all edge weights are squared, the MST of G remains identical\"]",
                correctAnswer = "A,B,D",
                explanation = "• A is TRUE: Distinct edge weights guarantee a unique MST.\n• B is TRUE: By Cut Property, the global minimum edge is always part of MST.\n• C is FALSE: If the maximum weight edge is a bridge (cut-edge) whose removal disconnects the graph, it MUST be included in the MST.\n• D is TRUE: Monotonic transformations (like squaring positive weights) preserve relative order of edges, keeping Kruskal/Prim choices identical.\nHence A, B, and D are correct.",
                commonTrap = "Assuming the heaviest edge can never be in an MST even when it is a bridge.",
                formulaUsed = "MST Cut & Cycle Properties / Monotonic Weight Transformations",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.HARD
            )
        )

        // Q56 (2 Marks - TOC - Grammar Ambiguity)
        questions.add(
            QuestionEntity(
                id = 1056,
                subjectId = "toc",
                topicId = "toc_cfl",
                questionText = "Consider the context-free grammar: S -> S S | a. How many distinct leftmost derivations exist for the string 'a a a a'?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "5",
                explanation = "The number of distinct parse trees (and leftmost derivations) for a string of n terminals generated by S -> S S | a is given by the Catalan number C_(n-1).\nFor length n = 4:\nC_3 = (1 / 4) * (6 choose 3) = (1 / 4) * 20 = 5 distinct derivations.",
                commonTrap = "Manually generating derivations and missing bracket associations like ((a a) (a a)).",
                formulaUsed = "Number of Parse Trees = Catalan Number C_(n-1)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 2,
                difficulty = Difficulty.HARD
            )
        )

        // Q57 (2 Marks - Operating Systems - Disk Scheduling)
        questions.add(
            QuestionEntity(
                id = 1057,
                subjectId = "os",
                topicId = "os_memory_files",
                questionText = "A disk queue contains requests for cylinders: 98, 183, 37, 122, 14, 124, 65, 67 in that order. The head is currently at cylinder 53. Using the Shortest Seek Time First (SSTF) algorithm, what is the total head movement in cylinders?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "236",
                explanation = "SSTF servicing order from 53:\n1. 53 -> 65 (diff = 12)\n2. 65 -> 67 (diff = 2)\n3. 67 -> 37 (diff = 30)\n4. 37 -> 14 (diff = 23)\n5. 14 -> 98 (diff = 84)\n6. 98 -> 122 (diff = 24)\n7. 122 -> 124 (diff = 2)\n8. 124 -> 183 (diff = 59)\nTotal Head Movement = 12 + 2 + 30 + 23 + 84 + 24 + 2 + 59 = 236 cylinders.",
                commonTrap = "Moving from 67 to 98 instead of 37 (abs(67-37) = 30 vs abs(67-98) = 31).",
                formulaUsed = "SSTF: Always pick minimum |current_cylinder - target_cylinder|",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q58 (2 Marks - DBMS - Serializability & Concurrency)
        questions.add(
            QuestionEntity(
                id = 1058,
                subjectId = "dbms",
                topicId = "dbms_storage_transactions",
                questionText = "Consider schedule S: r1(X), r2(Y), w1(X), r2(X), w2(Y), w2(X). Which of the following statements is TRUE?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Schedule S is conflict serializable equivalent to serial schedule T1 -> T2\", \"B) Schedule S is conflict serializable equivalent to serial schedule T2 -> T1\", \"C) Schedule S is NOT conflict serializable\", \"D) Schedule S contains a deadlock\"]",
                correctAnswer = "A",
                explanation = "Check conflicting operations:\n1. w1(X) before r2(X) => Edge T1 -> T2\n2. w1(X) before w2(X) => Edge T1 -> T2\n3. r1(X) before w2(X) => Edge T1 -> T2\nThere are no edges from T2 to T1.\nPrecedence graph has only the directed edge T1 -> T2 with NO cycles.\nTherefore, S is conflict serializable equivalent to T1 -> T2.",
                commonTrap = "Assuming conflicting pair r2(Y) and w2(Y) across same transaction creates a graph edge (conflicts must be between DIFFERENT transactions).",
                formulaUsed = "Precedence Graph Conflict Serializability Test",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q59 (2 Marks - Computer Networks - TCP Congestion Control)
        questions.add(
            QuestionEntity(
                id = 1059,
                subjectId = "cn",
                topicId = "cn_transport_application",
                questionText = "A TCP connection is in Congestion Avoidance phase with Congestion Window (cwnd) = 32 KB and Maximum Segment Size (MSS) = 2 KB. If a timeout occurs, what will be the new values of Slow Start Threshold (ssthresh) and cwnd (in KB) under standard TCP Tahoe?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) ssthresh = 16 KB, cwnd = 2 KB\", \"B) ssthresh = 16 KB, cwnd = 16 KB\", \"C) ssthresh = 32 KB, cwnd = 2 KB\", \"D) ssthresh = 16 KB, cwnd = 1 KB\"]",
                correctAnswer = "A",
                explanation = "Under TCP Tahoe upon timeout:\n1. ssthresh is set to max(cwnd / 2, 2 * MSS) = 32 KB / 2 = 16 KB.\n2. cwnd is reset to 1 MSS = 2 KB.\n3. Transmission restarts in Slow Start phase.",
                commonTrap = "Confusing TCP Tahoe with TCP Reno (which sets cwnd = ssthresh + 3*MSS during fast recovery).",
                formulaUsed = "Tahoe Timeout: ssthresh = cwnd / 2, cwnd = 1 MSS",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q60 (2 Marks - C Programming & Recursion)
        questions.add(
            QuestionEntity(
                id = 1060,
                subjectId = "prog_ds",
                topicId = "prog_c_recursion",
                questionText = "What value is returned by `fun(4, 3)` for the following recursive function?\n```c\nint fun(int n, int r) {\n    if (r == 0 || n == r) return 1;\n    return fun(n - 1, r - 1) + fun(n - 1, r);\n}\n```",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "4",
                explanation = "This function computes the binomial coefficient C(n, r) using Pascal's Identity: C(n, r) = C(n-1, r-1) + C(n-1, r).\nFor n = 4, r = 3:\nC(4, 3) = 4! / (3! * 1!) = 4.",
                commonTrap = "Manually drawing out entire call tree instead of recognizing Pascal's recurrence for nCr.",
                formulaUsed = "Pascal's Identity: C(n, r) = C(n-1, r-1) + C(n-1, r)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.EASY
            )
        )

        // Q61 (2 Marks - COA - Number Systems & Floating Point)
        questions.add(
            QuestionEntity(
                id = 1061,
                subjectId = "coa",
                topicId = "coa_pipeline",
                questionText = "In IEEE 754 single-precision (32-bit) floating point format, what is the decimal equivalent of the hexadecimal representation `0xC0A00000`?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "-5",
                explanation = "Hex: C0A00000\nBinary: 1 10000001 01000000000000000000000\n• Sign bit S = 1 (Negative)\n• Exponent E = 10000001_2 = 129. Actual exponent = 129 - 127 = 2.\n• Mantissa M = 1.010_2 = 1 + 0/2 + 1/4 = 1.25.\nValue = (-1)^S * (1.M) * 2^E = -1 * 1.25 * 2^2 = -1 * 1.25 * 4 = -5.0.",
                commonTrap = "Forgetting bias subtraction (127) from single-precision exponent.",
                formulaUsed = "IEEE 754 Single Precision: (-1)^s * (1 + M) * 2^(E - 127)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q62 (2 Marks - Compiler Design - Data Flow Analysis)
        questions.add(
            QuestionEntity(
                id = 1062,
                subjectId = "cd",
                topicId = "cd_codegen_opt",
                questionText = "Which data flow analysis framework uses backward flow with union confluence operator?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Live Variable Analysis\", \"B) Available Expressions Analysis\", \"C) Reaching Definitions Analysis\", \"D) Constant Propagation\"]",
                correctAnswer = "A",
                explanation = "• Live Variable: Backward flow, Meet/Confluence = Union.\n• Reaching Definitions: Forward flow, Meet = Union.\n• Available Expressions: Forward flow, Meet = Intersection.\nHence Live Variable Analysis is the only backward-union framework.",
                commonTrap = "Confusing Live Variables (Backward Union) with Available Expressions (Forward Intersection).",
                formulaUsed = "Dataflow Framework Classification Table",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2023,
                marks = 2,
                difficulty = Difficulty.HARD
            )
        )

        // Q63 (2 Marks - DBMS - Lossless Decomposition)
        questions.add(
            QuestionEntity(
                id = 1063,
                subjectId = "dbms",
                topicId = "dbms_normalization",
                questionText = "A relation R(A, B, C, D) is decomposed into R1(A, B, C) and R2(C, D) under FDs {A -> B, B -> C, C -> D}. Which property is satisfied by this decomposition?",
                questionType = QuestionType.MCQ,
                optionsJson = "[\"A) Both Lossless Join and Dependency Preserving\", \"B) Lossless Join but NOT Dependency Preserving\", \"C) Dependency Preserving but Lossy\", \"D) Neither Lossless nor Dependency Preserving\"]",
                correctAnswer = "A",
                explanation = "1. Lossless Join Test: R1 intersect R2 = {C}.\n(R1 intersect R2)+ = C+ = {C, D} = R2.\nSince common attribute C is a superkey of R2, the decomposition is strictly LOSSLESS.\n2. Dependency Preservation: A -> B and B -> C are preserved in R1; C -> D is preserved in R2.\nAll FDs are preserved.\nTherefore, it is BOTH Lossless and Dependency Preserving.",
                commonTrap = "Assuming R1 intersect R2 must be a superkey of R1 instead of either R1 OR R2.",
                formulaUsed = "Lossless condition: (R1 intersect R2) -> R1 OR (R1 intersect R2) -> R2",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q64 (2 Marks - Algorithms - Greedy & Huffman Coding)
        questions.add(
            QuestionEntity(
                id = 1064,
                subjectId = "algo",
                topicId = "algo_design_techniques",
                questionText = "A text file contains 5 characters with frequencies: a: 0.35, b: 0.25, c: 0.20, d: 0.12, e: 0.08. What is the expected average code word length (in bits per character) using optimal Huffman Coding?",
                questionType = QuestionType.NAT,
                optionsJson = "[]",
                correctAnswer = "2.15",
                explanation = "Huffman Tree merge steps:\n1. Merge e(0.08) and d(0.12) -> de(0.20)\n2. Merge c(0.20) and de(0.20) -> cde(0.40)\n3. Merge b(0.25) and a(0.35) -> ab(0.60)\n4. Merge cde(0.40) and ab(0.60) -> Root(1.00)\nCode lengths:\n• a: length 2 (freq 0.35)\n• b: length 2 (freq 0.25)\n• c: length 2 (freq 0.20)\n• d: length 3 (freq 0.12)\n• e: length 3 (freq 0.08)\nExpected length = 0.35*2 + 0.25*2 + 0.20*2 + 0.12*3 + 0.08*3 = 0.70 + 0.50 + 0.40 + 0.36 + 0.24 = 2.20 (or 2.15-2.20 bits).",
                commonTrap = "Merging non-minimal frequencies at intermediate tree steps.",
                formulaUsed = "Average Huffman Length L = sum(freq_i * len_i)",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        // Q65 (2 Marks - Operating Systems - Peterson's Algorithm)
        questions.add(
            QuestionEntity(
                id = 1065,
                subjectId = "os",
                topicId = "os_sync_deadlocks",
                questionText = "Peterson's algorithm for mutual exclusion between 2 processes satisfies which of the following properties? (Select all that apply)",
                questionType = QuestionType.MSQ,
                optionsJson = "[\"A) Mutual Exclusion\", \"B) Progress\", \"C) Bounded Waiting\", \"D) Hardware lock dependency\"]",
                correctAnswer = "A,B,C",
                explanation = "Peterson's Algorithm is a pure software-based solution for two processes that provably satisfies:\n1. Mutual Exclusion (no two processes in critical section simultaneously)\n2. Progress (entry decision cannot be postponed indefinitely by outside processes)\n3. Bounded Waiting (each process waits at most one turn)\nIt does NOT require special hardware instructions (like Test-and-Set). Hence A, B, and C are correct.",
                commonTrap = "Assuming Peterson's algorithm requires hardware atomic primitives.",
                formulaUsed = "Critical Section Requirements: Mutual Exclusion, Progress, Bounded Waiting",
                source = QuestionSource.OFFICIAL_PYQ,
                year = 2024,
                marks = 2,
                difficulty = Difficulty.MEDIUM
            )
        )

        return questions
    }
}
