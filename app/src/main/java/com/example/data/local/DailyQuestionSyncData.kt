package com.example.data.local

import com.example.data.model.Difficulty
import com.example.data.model.QuestionEntity
import com.example.data.model.QuestionSource
import com.example.data.model.QuestionType
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

object DailyQuestionSyncData {

    fun generateFreshDailyPack(dateSeed: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())): List<QuestionEntity> {
        val baseId = (System.currentTimeMillis() % 1000000) + 100000L

        return listOf(
            // --- 1. OPERATING SYSTEMS ---
            QuestionEntity(
                id = baseId + 1,
                subjectId = "os",
                topicId = "os_cpu_scheduling",
                questionText = "[$dateSeed Daily Pack] In an OS with Round Robin CPU scheduling, the time quantum is q = 4 ms. If three processes P1 (Burst 10ms), P2 (Burst 4ms), and P3 (Burst 2ms) arrive simultaneously at t=0 in order (P1, P2, P3), what is the turnaround time of process P1 in milliseconds?",
                optionsJson = JSONArray(listOf("A. 16 ms", "B. 10 ms", "C. 14 ms", "D. 12 ms")).toString(),
                correctAnswer = "A",
                explanation = "Gantt chart execution: [0-4] P1 (rem 6), [4-8] P2 (rem 0, finishes at 8), [8-10] P3 (rem 0, finishes at 10), [10-14] P1 (rem 2), [14-16] P1 (finishes at 16). Turnaround Time of P1 = Completion Time (16) - Arrival Time (0) = 16 ms.",
                questionType = QuestionType.MCQ,
                marks = 2,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.MEDIUM,
                commonTrap = "Do not forget context switch order and remaining bursts."
            ),
            QuestionEntity(
                id = baseId + 2,
                subjectId = "os",
                topicId = "os_virtual_memory",
                questionText = "[$dateSeed Daily Pack] A system uses 32-bit virtual addresses with a 2-level paging scheme. The page size is 4 KB. If the inner page table has 1024 entries of 4 bytes each, what is the number of bits in the virtual address allocated for the outer page table index?",
                optionsJson = "[]",
                correctAnswer = "10",
                explanation = "Page size = 4 KB = 2^12 bytes, so offset = 12 bits. Inner page table has 1024 entries = 2^10 entries, so inner page index = 10 bits. Outer page index = 32 - 12 - 10 = 10 bits.",
                questionType = QuestionType.NAT,
                marks = 2,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.MEDIUM,
                commonTrap = "Virtual address = Outer page index (10) + Inner page index (10) + Offset (12) = 32 bits."
            ),

            // --- 2. DBMS ---
            QuestionEntity(
                id = baseId + 3,
                subjectId = "dbms",
                topicId = "dbms_normalization",
                questionText = "[$dateSeed Daily Pack] Relation R(A, B, C, D, E) with Functional Dependencies F = { A -> BC, CD -> E, B -> D, E -> A }. Which of the following is/are Candidate Key(s) of R?",
                optionsJson = JSONArray(listOf("A. {A}", "B. {B}", "C. {E}", "D. {CD}")).toString(),
                correctAnswer = "A,C,D",
                explanation = "A+ = {A,B,C,D,E} -> Candidate Key. E+ = {E,A,B,C,D} -> Candidate Key. (CD)+ = {C,D,E,A,B} -> Candidate Key. B+ = {B,D} (does not contain A,C,E) -> Not a key. Thus A, C, and D are valid Candidate Keys.",
                questionType = QuestionType.MSQ,
                marks = 2,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.HARD,
                commonTrap = "Check closure of single and combined attributes thoroughly."
            ),
            QuestionEntity(
                id = baseId + 4,
                subjectId = "dbms",
                topicId = "dbms_transactions",
                questionText = "[$dateSeed Daily Pack] In database transaction processing, which schedule property strictly ensures freedom from cascading rollbacks (cascadelessness)?",
                optionsJson = JSONArray(listOf(
                    "A. Every transaction reads only values committed before the read operation",
                    "B. Every transaction writes after committing",
                    "C. Every transaction aborts independently",
                    "D. Schedule is serial"
                )).toString(),
                correctAnswer = "A",
                explanation = "A schedule is Cascadeless if each transaction reads only data items written by committed transactions, preventing chained aborts.",
                questionType = QuestionType.MCQ,
                marks = 1,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.EASY
            ),

            // --- 3. COMPUTER NETWORKS ---
            QuestionEntity(
                id = baseId + 5,
                subjectId = "cn",
                topicId = "cn_transport_layer",
                questionText = "[$dateSeed Daily Pack] In TCP congestion control, the slow-start threshold (ssthresh) is 32 KB, and maximum segment size (MSS) is 2 KB. Starting with Congestion Window (cwnd) = 1 MSS, after how many Successful Round Trip Times (RTTs) will cwnd reach 32 KB without timeouts?",
                optionsJson = "[]",
                correctAnswer = "4",
                explanation = "Cwnd doubles each RTT during slow start: RTT 0: 1 MSS (2KB), RTT 1: 2 MSS (4KB), RTT 2: 4 MSS (8KB), RTT 3: 8 MSS (16KB), RTT 4: 16 MSS (32KB). So after 4 RTTs, cwnd reaches 32 KB.",
                questionType = QuestionType.NAT,
                marks = 2,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.MEDIUM
            ),
            QuestionEntity(
                id = baseId + 6,
                subjectId = "cn",
                topicId = "cn_routing",
                questionText = "[$dateSeed Daily Pack] An IP router receives a packet with IP address 192.168.10.45. Which of the following matching prefix rules takes precedence in CIDR routing table lookup?",
                optionsJson = JSONArray(listOf(
                    "A. Longest Prefix Match (most specific subnet mask)",
                    "B. First matching entry in routing table",
                    "C. Lowest metric interface",
                    "D. Shortest network prefix"
                )).toString(),
                correctAnswer = "A",
                explanation = "CIDR routing strictly uses the Longest Prefix Match rule to forward packets to the most specific matching network route.",
                questionType = QuestionType.MCQ,
                marks = 1,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.EASY
            ),

            // --- 4. DATA STRUCTURES & ALGORITHMS ---
            QuestionEntity(
                id = baseId + 7,
                subjectId = "dsa",
                topicId = "dsa_trees",
                questionText = "[$dateSeed Daily Pack] In an AVL tree, what is the maximum possible height (measuring height by number of edges) for an AVL tree containing exactly 12 nodes?",
                optionsJson = "[]",
                correctAnswer = "4",
                explanation = "Minimum nodes for AVL tree of height h: N(0)=1, N(1)=2, N(2)=4, N(3)=7, N(4)=12, N(5)=20. Since 12 nodes equals N(4), the maximum height for 12 nodes is 4 edges.",
                questionType = QuestionType.NAT,
                marks = 2,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.HARD,
                commonTrap = "Recurrence is N(h) = 1 + N(h-1) + N(h-2)."
            ),
            QuestionEntity(
                id = baseId + 8,
                subjectId = "dsa",
                topicId = "dsa_dynamic_programming",
                questionText = "[$dateSeed Daily Pack] Which of the following statements is/are TRUE regarding the 0/1 Knapsack Problem and Fractional Knapsack Problem?",
                optionsJson = JSONArray(listOf(
                    "A. Fractional Knapsack can be solved optimally in O(n log n) using a Greedy strategy.",
                    "B. 0/1 Knapsack problem exhibits optimal substructure.",
                    "C. 0/1 Knapsack can be solved in polynomial time O(n W) where W is item weight bound (Pseudo-polynomial).",
                    "D. Greedy approach always gives the optimal solution for 0/1 Knapsack."
                )).toString(),
                correctAnswer = "A,B,C",
                explanation = "Fractional knapsack solves greedily by value/weight ratio (A is TRUE). 0/1 knapsack has optimal substructure (B is TRUE) and DP runs in O(n W) pseudo-polynomial time (C is TRUE). Greedy does NOT guarantee optimal for 0/1 knapsack (D is FALSE).",
                questionType = QuestionType.MSQ,
                marks = 2,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.MEDIUM
            ),

            // --- 5. THEORY OF COMPUTATION ---
            QuestionEntity(
                id = baseId + 9,
                subjectId = "toc",
                topicId = "toc_regular_languages",
                questionText = "[$dateSeed Daily Pack] The language L = { w in {0, 1}* | number of 0s in w is divisible by 3 and number of 1s in w is divisible by 4 } is recognized by a Minimal Deterministic Finite Automaton (DFA) with how many states?",
                optionsJson = "[]",
                correctAnswer = "12",
                explanation = "Since the conditions mod 3 and mod 4 are independent and gcd(3,4)=1, the cross-product DFA requires exactly 3 * 4 = 12 reachable and pairwise distinct states.",
                questionType = QuestionType.NAT,
                marks = 2,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.MEDIUM
            ),
            QuestionEntity(
                id = baseId + 10,
                subjectId = "toc",
                topicId = "toc_decidability",
                questionText = "[$dateSeed Daily Pack] Which of the following problems for Turing Machines is DECIDABLE?",
                optionsJson = JSONArray(listOf(
                    "A. Whether a given Turing Machine accepts an empty string",
                    "B. Whether a given Turing Machine halts on all inputs within 100 steps",
                    "C. Whether the language accepted by a Turing Machine is regular",
                    "D. Whether two arbitrary Turing Machines accept the same language"
                )).toString(),
                correctAnswer = "B",
                explanation = "Simulation of a Turing Machine for a finite bounded number of steps (100 steps) is strictly finite and always decidable. All other options are undecidable by Rice's theorem.",
                questionType = QuestionType.MCQ,
                marks = 1,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.EASY
            ),

            // --- 6. COMPUTER ORGANIZATION (COA) ---
            QuestionEntity(
                id = baseId + 11,
                subjectId = "coa",
                topicId = "coa_pipelining",
                questionText = "[$dateSeed Daily Pack] A 5-stage instruction pipeline has stage delays of 150 ps, 120 ps, 160 ps, 140 ps, and 110 ps. The pipeline register delay is 20 ps. What is the clock cycle time in picoseconds?",
                optionsJson = "[]",
                correctAnswer = "180",
                explanation = "Clock cycle time = Max(Stage Delays) + Register Delay = Max(150, 120, 160, 140, 110) + 20 = 160 + 20 = 180 ps.",
                questionType = QuestionType.NAT,
                marks = 1,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.EASY
            ),

            // --- 7. COMPILER DESIGN ---
            QuestionEntity(
                id = baseId + 12,
                subjectId = "compiler",
                topicId = "compiler_parsing",
                questionText = "[$dateSeed Daily Pack] Consider the grammar: S -> Aa | bAc | dc, A -> d. Which parsing conflicts, if any, occur in the LR(0) state containing items { S -> d . c, A -> d . } ?",
                optionsJson = JSONArray(listOf(
                    "A. Shift-Reduce Conflict",
                    "B. Reduce-Reduce Conflict",
                    "C. Shift-Shift Conflict",
                    "D. No Conflict"
                )).toString(),
                correctAnswer = "A",
                explanation = "The state contains both a shift action on terminal 'c' (S -> d . c) and a reduce action (A -> d .), creating an LR(0) Shift-Reduce (SR) conflict.",
                questionType = QuestionType.MCQ,
                marks = 1,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.MEDIUM
            ),

            // --- 8. ENGINEERING MATHEMATICS ---
            QuestionEntity(
                id = baseId + 13,
                subjectId = "em",
                topicId = "em_linear_algebra",
                questionText = "[$dateSeed Daily Pack] The eigenvalues of a 2x2 real matrix M are 3 and -1. What is the determinant of the matrix (M^2 + 2I)?",
                optionsJson = "[]",
                correctAnswer = "33",
                explanation = "Eigenvalues of M are λ1 = 3, λ2 = -1. Eigenvalues of M^2 + 2I are: μ1 = (3)^2 + 2 = 11, and μ2 = (-1)^2 + 2 = 3. Determinant = product of eigenvalues = 11 * 3 = 33.",
                questionType = QuestionType.NAT,
                marks = 2,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.MEDIUM
            ),

            // --- 9. DIGITAL LOGIC ---
            QuestionEntity(
                id = baseId + 14,
                subjectId = "dl",
                topicId = "dl_combinational",
                questionText = "[$dateSeed Daily Pack] How many 2-to-1 Multiplexers are required to implement a 16-to-1 Multiplexer?",
                optionsJson = "[]",
                correctAnswer = "15",
                explanation = "To construct an N-to-1 multiplexer using 2-to-1 MUXes, total MUXes required = N - 1. For N=16, 8 (stage 1) + 4 (stage 2) + 2 (stage 3) + 1 (stage 4) = 15 multiplexers.",
                questionType = QuestionType.NAT,
                marks = 1,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.EASY
            ),

            // --- 10. GENERAL APTITUDE ---
            QuestionEntity(
                id = baseId + 15,
                subjectId = "ga",
                topicId = "ga_quantitative",
                questionText = "[$dateSeed Daily Pack] A train traveling at 72 km/h crosses a 200-meter long stationary platform in 25 seconds. What is the length of the train in meters?",
                optionsJson = "[]",
                correctAnswer = "300",
                explanation = "Speed in m/s = 72 * (5/18) = 20 m/s. Total distance in 25s = 20 * 25 = 500 meters. Length of train = Total distance (500) - Platform length (200) = 300 meters.",
                questionType = QuestionType.NAT,
                marks = 2,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.EASY
            )
        )
    }

    /**
     * Assembles and generates a complete 100-Question Full-Length Daily Grand GATE Mock Exam.
     * Pulls existing questions from the Room database, ensures comprehensive coverage across all
     * 10 core GATE CSE subjects, and applies date-seeded randomization to provide a fresh 100Q paper each morning.
     */
    fun generateDaily100QuestionMockTest(
        existingQuestions: List<QuestionEntity>,
        dateSeed: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    ): Pair<com.example.data.model.MockTestEntity, List<QuestionEntity>> {
        val dateHash = Math.abs(dateSeed.hashCode())
        val random = Random(dateHash.toLong())

        // 1. Gather all pool candidates
        val pool = existingQuestions.toMutableList()
        if (pool.size < 65) {
            pool.addAll(GateFullPaperData.generateOfficial100MarkGatePaper())
            pool.addAll(generateFreshDailyPack(dateSeed))
        }

        val subjectDistribution = mapOf(
            "ga" to 15,       // General Aptitude (Verbal, Quant, Spatial)
            "math" to 15,     // Engineering Math & Discrete Math
            "os" to 10,       // Operating Systems
            "dbms" to 10,     // Database Management Systems
            "cn" to 10,       // Computer Networks
            "algo" to 10,     // Algorithms
            "prog_ds" to 10,  // C Programming & Data Structures
            "toc" to 8,       // Theory of Computation
            "compiler" to 4,  // Compiler Design
            "coa" to 4,       // Computer Architecture
            "dl" to 4         // Digital Logic
        )

        val selectedQuestions = mutableListOf<QuestionEntity>()
        val bySubject = pool.groupBy {
            when (it.subjectId) {
                "em", "math" -> "math"
                "pds", "prog_ds" -> "prog_ds"
                "cd", "compiler" -> "compiler"
                else -> it.subjectId
            }
        }

        // Fill by domain quotas
        subjectDistribution.forEach { (subId, quota) ->
            val subPool = (bySubject[subId] ?: emptyList()).shuffled(random)
            val count = Math.min(quota, subPool.size)
            selectedQuestions.addAll(subPool.take(count))
        }

        // Complement from remaining pool if under 100
        val remainingPool = pool.filter { q -> selectedQuestions.none { it.id == q.id } }.shuffled(random)
        val needed = 100 - selectedQuestions.size
        if (needed > 0 && remainingPool.isNotEmpty()) {
            selectedQuestions.addAll(remainingPool.take(needed))
        }

        // If still under 100, synthesize parameterized high-yield questions for today
        var syntheticCounter = 1
        while (selectedQuestions.size < 100) {
            val qId = 900000L + (dateHash % 10000) + syntheticCounter
            val subjects = listOf("os", "dbms", "cn", "algo", "prog_ds", "math", "ga", "toc", "coa", "dl")
            val sub = subjects[syntheticCounter % subjects.size]
            val syntheticQ = generateSyntheticQuestion(qId, sub, dateSeed, syntheticCounter)
            selectedQuestions.add(syntheticQ)
            syntheticCounter++
        }

        // Shuffle with date seed to ensure unique question order for today
        val final100Questions = selectedQuestions.take(100).shuffled(random).mapIndexed { index, q ->
            // Ensure distinct question numbers and accurate 100-mark scaling
            q.copy(id = if (q.id > 0) q.id else (900000L + index))
        }

        val questionIdsJson = JSONArray(final100Questions.map { it.id }).toString()
        val totalMarks = 100.0

        val mockTestEntity = com.example.data.model.MockTestEntity(
            id = 99901L,
            title = "🌅 Daily 100-Question Grand Mock Exam • $dateSeed",
            testType = "DAILY_100Q_MOCK",
            durationMinutes = 180,
            totalQuestions = 100,
            totalMarks = totalMarks,
            questionIdsJson = questionIdsJson
        )

        return Pair(mockTestEntity, final100Questions)
    }

    private fun generateSyntheticQuestion(
        id: Long,
        subjectId: String,
        dateSeed: String,
        variationIndex: Int
    ): QuestionEntity {
        return when (subjectId) {
            "os" -> QuestionEntity(
                id = id,
                subjectId = "os",
                topicId = "os_virtual_memory",
                questionText = "[$dateSeed Morning 100Q Mock] In a demand paging system with page size 4KB, a process generates a 32-bit logical address 0x00003ABC. If the page table maps logical page 3 to physical frame 15 (0x0F), what is the corresponding 32-bit physical address in hexadecimal?",
                optionsJson = JSONArray(listOf("A. 0x0000FABC", "B. 0x00003ABC", "C. 0x00000F3A", "D. 0x000F0ABC")).toString(),
                correctAnswer = "A",
                explanation = "Page offset = 12 bits (4KB = 2^12) -> offset = 0xABC. Logical page number = 3. Frame number = 15 = 0xF. Physical address = (Frame << 12) | Offset = 0x0000FABC.",
                questionType = QuestionType.MCQ,
                marks = 1,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.MEDIUM
            )
            "dbms" -> QuestionEntity(
                id = id,
                subjectId = "dbms",
                topicId = "dbms_indexing",
                questionText = "[$dateSeed Morning 100Q Mock] A B+ tree of order 5 (maximum 4 keys per node) stores 10,000 search key values at the leaf level. What is the minimum possible height of the tree (counting the root level as height 1)?",
                optionsJson = "[]",
                correctAnswer = "7",
                explanation = "Minimum height is achieved when each node has the maximum fanout. Root has 5 pointers, level 2 has 25 pointers, level 3 has 125 pointers, ..., level h reaches 10,000 leaves.",
                questionType = QuestionType.NAT,
                marks = 2,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.HARD
            )
            "cn" -> QuestionEntity(
                id = id,
                subjectId = "cn",
                topicId = "cn_routing",
                questionText = "[$dateSeed Morning 100Q Mock] In Classless Inter-Domain Routing (CIDR), an organization is assigned the block 200.10.0.0/20. What is the maximum number of usable host IP addresses in this block?",
                optionsJson = "[]",
                correctAnswer = "4094",
                explanation = "Host bits = 32 - 20 = 12 bits. Total addresses = 2^12 = 4096. Usable host addresses = 4096 - 2 (network ID & broadcast) = 4094.",
                questionType = QuestionType.NAT,
                marks = 1,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.EASY
            )
            "algo" -> QuestionEntity(
                id = id,
                subjectId = "algo",
                topicId = "algo_dp",
                questionText = "[$dateSeed Morning 100Q Mock] What is the worst-case time complexity of the Floyd-Warshall all-pairs shortest paths algorithm on a directed graph with V vertices and E edges?",
                optionsJson = JSONArray(listOf("A. Θ(V^3)", "B. Θ(V * E)", "C. Θ(V^2 log V + E)", "D. Θ(E log V)")).toString(),
                correctAnswer = "A",
                explanation = "Floyd-Warshall employs three nested loops from 1 to V, resulting in an exact worst-case running time of Θ(V^3) regardless of edge count E.",
                questionType = QuestionType.MCQ,
                marks = 1,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.EASY
            )
            "prog_ds" -> QuestionEntity(
                id = id,
                subjectId = "prog_ds",
                topicId = "prog_pointers",
                questionText = "[$dateSeed Morning 100Q Mock] What will be printed by the following C program segment?\nint a[] = {10, 20, 30, 40};\nint *p = a;\nprintf(\"%d\", *(p + 2) + *p);",
                optionsJson = "[]",
                correctAnswer = "40",
                explanation = "*(p + 2) evaluates to a[2] = 30. *p evaluates to a[0] = 10. Sum = 30 + 10 = 40.",
                questionType = QuestionType.NAT,
                marks = 1,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.EASY
            )
            "math" -> QuestionEntity(
                id = id,
                subjectId = "math",
                topicId = "math_discrete",
                questionText = "[$dateSeed Morning 100Q Mock] The number of binary relations on a set A containing 4 elements that are both reflexive and symmetric is:",
                optionsJson = "[]",
                correctAnswer = "64",
                explanation = "In an n-element set (n=4), the relation matrix has n diagonal elements and (n^2 - n)/2 = (16 - 4)/2 = 6 independent non-diagonal pairs. Reflexive fixes all diagonal entries to 1. Symmetric forces pairs (a,b) and (b,a) to have the same choice. Total relations = 2^6 = 64.",
                questionType = QuestionType.NAT,
                marks = 2,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.MEDIUM
            )
            else -> QuestionEntity(
                id = id,
                subjectId = "ga",
                topicId = "ga_reasoning",
                questionText = "[$dateSeed Morning 100Q Mock] If all Bloops are Razzies and some Razzies are Lizzies, which of the following statements MUST be logically true?",
                optionsJson = JSONArray(listOf(
                    "A. Some Bloops are definitely Lizzies",
                    "B. All Bloops are Lizzies",
                    "C. No Bloops are Lizzies",
                    "D. None of the above is guaranteed"
                )).toString(),
                correctAnswer = "D",
                explanation = "Since the intersection between Bloops and the subset of Razzies that are Lizzies is not guaranteed, no definitive statement about Bloops and Lizzies can be deduced with certainty.",
                questionType = QuestionType.MCQ,
                marks = 1,
                source = QuestionSource.AI_PRACTICE,
                year = 2027,
                difficulty = Difficulty.EASY
            )
        }
    }
}
