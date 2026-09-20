package com.example.data.local

import com.example.data.model.*
import java.text.SimpleDateFormat
import java.util.*

object SeedData {

    fun getInitialSubjects(): List<SubjectEntity> = listOf(
        SubjectEntity(
            id = "math",
            name = "Engineering Mathematics",
            code = "EM",
            iconName = "Calculate",
            totalWeightage = 13.0,
            colorHex = "#3B82F6",
            orderIndex = 1
        ),
        SubjectEntity(
            id = "dl",
            name = "Digital Logic",
            code = "DL",
            iconName = "Memory",
            totalWeightage = 6.0,
            colorHex = "#10B981",
            orderIndex = 2
        ),
        SubjectEntity(
            id = "coa",
            name = "Computer Organization & Architecture",
            code = "COA",
            iconName = "DeveloperBoard",
            totalWeightage = 8.0,
            colorHex = "#F59E0B",
            orderIndex = 3
        ),
        SubjectEntity(
            id = "prog_ds",
            name = "Programming & Data Structures",
            code = "PDS",
            iconName = "Code",
            totalWeightage = 10.0,
            colorHex = "#8B5CF6",
            orderIndex = 4
        ),
        SubjectEntity(
            id = "algo",
            name = "Algorithms",
            code = "ALGO",
            iconName = "AccountTree",
            totalWeightage = 9.0,
            colorHex = "#EC4899",
            orderIndex = 5
        ),
        SubjectEntity(
            id = "toc",
            name = "Theory of Computation",
            code = "TOC",
            iconName = "AutoAwesome",
            totalWeightage = 8.0,
            colorHex = "#06B6D4",
            orderIndex = 6
        ),
        SubjectEntity(
            id = "cd",
            name = "Compiler Design",
            code = "CD",
            iconName = "Translate",
            totalWeightage = 5.0,
            colorHex = "#14B8A6",
            orderIndex = 7
        ),
        SubjectEntity(
            id = "os",
            name = "Operating Systems",
            code = "OS",
            iconName = "Computer",
            totalWeightage = 9.0,
            colorHex = "#6366F1",
            orderIndex = 8
        ),
        SubjectEntity(
            id = "dbms",
            name = "Databases (DBMS)",
            code = "DBMS",
            iconName = "Storage",
            totalWeightage = 8.0,
            colorHex = "#F97316",
            orderIndex = 9
        ),
        SubjectEntity(
            id = "cn",
            name = "Computer Networks",
            code = "CN",
            iconName = "Lan",
            totalWeightage = 9.0,
            colorHex = "#00E5FF",
            orderIndex = 10
        )
    )

    fun getInitialTopics(): List<TopicEntity> = listOf(
        // 1. Engineering Mathematics
        TopicEntity(
            id = "math_discrete",
            subjectId = "math",
            name = "Discrete Mathematics & Logic",
            subtopicsList = "Propositional Logic, First Order Logic, Sets, Relations, Functions, Partial Orders, Lattices, Monoids, Groups",
            importance = "HIGH",
            estimatedHours = 8,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "math_graph_theory",
            subjectId = "math",
            name = "Graph Theory & Combinatorics",
            subtopicsList = "Connectivity, Matching, Graph Coloring, Counting Techniques, Recurrence Relations, Generating Functions",
            importance = "HIGH",
            estimatedHours = 7,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "math_linear_algebra",
            subjectId = "math",
            name = "Linear Algebra",
            subtopicsList = "Matrices, Determinants, Systems of Linear Equations, Eigenvalues and Eigenvectors, LU Decomposition",
            importance = "HIGH",
            estimatedHours = 6,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "math_calculus",
            subjectId = "math",
            name = "Calculus",
            subtopicsList = "Limits, Continuity, Differentiability, Maxima and Minima, Mean Value Theorem, Integration",
            importance = "MEDIUM",
            estimatedHours = 5,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "math_probability",
            subjectId = "math",
            name = "Probability & Statistics",
            subtopicsList = "Random Variables, Uniform/Normal/Exponential/Poisson/Binomial Distributions, Mean, Median, Mode, Variance, Conditional Probability, Bayes Theorem",
            importance = "HIGH",
            estimatedHours = 7,
            masteryLevel = 0.0
        ),

        // 2. Digital Logic
        TopicEntity(
            id = "dl_boolean_circuits",
            subjectId = "dl",
            name = "Boolean Algebra & Logic Minimization",
            subtopicsList = "Boolean Algebra, Logic Minimization, K-Maps, Combinational Circuits (Adders, Multiplexers, Decoders)",
            importance = "HIGH",
            estimatedHours = 5,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "dl_sequential",
            subjectId = "dl",
            name = "Sequential Circuits & Arithmetic",
            subtopicsList = "Sequential Circuits, Latches, Flip-Flops, Counters, Number Representation, Computer Arithmetic, Fixed & Floating Point",
            importance = "HIGH",
            estimatedHours = 6,
            masteryLevel = 0.0
        ),

        // 3. Computer Organization & Architecture
        TopicEntity(
            id = "coa_machine_datapath",
            subjectId = "coa",
            name = "Machine Instructions & Datapath",
            subtopicsList = "Machine Instructions, Addressing Modes, ALU, Data Path, Control Unit (Hardwired & Microprogrammed)",
            importance = "HIGH",
            estimatedHours = 6,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "coa_pipeline",
            subjectId = "coa",
            name = "Instruction Pipelining & Hazards",
            subtopicsList = "Pipeline Concept, Structural/Data/Control Hazards, Speedup, Branch Penalty, Operand Forwarding",
            importance = "HIGH",
            estimatedHours = 7,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "coa_cache_memory",
            subjectId = "coa",
            name = "Memory Hierarchy & I/O",
            subtopicsList = "Cache Memory (Direct, Set-Associative, Fully Associative), Main Memory, Secondary Storage, Interrupts, DMA",
            importance = "HIGH",
            estimatedHours = 7,
            masteryLevel = 0.0
        ),

        // 4. Programming & Data Structures
        TopicEntity(
            id = "prog_c_recursion",
            subjectId = "prog_ds",
            name = "C Programming & Recursion",
            subtopicsList = "Programming in C, Control Statements, Pointers, Arrays, Strings, Functions, Recursion, Parameter Passing",
            importance = "HIGH",
            estimatedHours = 7,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "ds_linear",
            subjectId = "prog_ds",
            name = "Linear Data Structures",
            subtopicsList = "Arrays, Stacks, Queues, Linked Lists, Evaluation of Expressions, Infix to Postfix",
            importance = "HIGH",
            estimatedHours = 5,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "ds_trees_graphs",
            subjectId = "prog_ds",
            name = "Trees, Heaps & Graphs",
            subtopicsList = "Binary Trees, Binary Search Trees (BST), AVL Trees, Binary Heaps, Priority Queues, Graph Representations",
            importance = "HIGH",
            estimatedHours = 8,
            masteryLevel = 0.0
        ),

        // 5. Algorithms
        TopicEntity(
            id = "algo_asymptotic",
            subjectId = "algo",
            name = "Asymptotic Analysis & Recurrences",
            subtopicsList = "Asymptotic Time Complexity (Big-O, Omega, Theta), Space Complexity, Worst Case Analysis, Master Theorem, Akra-Bazzi",
            importance = "HIGH",
            estimatedHours = 5,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "algo_searching_sorting",
            subjectId = "algo",
            name = "Searching, Sorting & Hashing",
            subtopicsList = "Binary Search, Merge Sort, Quick Sort, Heap Sort, Counting Sort, Hashing & Collision Resolution",
            importance = "HIGH",
            estimatedHours = 6,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "algo_design_techniques",
            subjectId = "algo",
            name = "Algorithm Design Techniques",
            subtopicsList = "Greedy Algorithms, Dynamic Programming (0/1 Knapsack, LCS, Matrix Chain), Divide and Conquer",
            importance = "HIGH",
            estimatedHours = 9,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "algo_graphs",
            subjectId = "algo",
            name = "Graph Algorithms",
            subtopicsList = "Graph Traversals (BFS, DFS), Minimum Spanning Tree (Prim's, Kruskal's), Shortest Paths (Dijkstra, Bellman-Ford, Floyd-Warshall)",
            importance = "HIGH",
            estimatedHours = 8,
            masteryLevel = 0.0
        ),

        // 6. Theory of Computation
        TopicEntity(
            id = "toc_regular",
            subjectId = "toc",
            name = "Regular Expressions & Finite Automata",
            subtopicsList = "Regular Expressions, DFA, NFA, DFA Minimization (Myhill-Nerode), Regular Languages, Pumping Lemma for Regular",
            importance = "HIGH",
            estimatedHours = 8,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "toc_cfl",
            subjectId = "toc",
            name = "Grammars & Pushdown Automata",
            subtopicsList = "Context-Free Grammars (CFG), Pushdown Automata (PDA), Context-Free Languages, Ambiguity, Pumping Lemma for CFL",
            importance = "HIGH",
            estimatedHours = 7,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "toc_decidability",
            subjectId = "toc",
            name = "Turing Machines & Undecidability",
            subtopicsList = "Turing Machines, Computability, Halting Problem, Decidable vs Undecidable, Rice's Theorem, Post Correspondence Problem",
            importance = "HIGH",
            estimatedHours = 6,
            masteryLevel = 0.0
        ),

        // 7. Compiler Design
        TopicEntity(
            id = "cd_lexical_parsing",
            subjectId = "cd",
            name = "Lexical & Syntax Analysis",
            subtopicsList = "Compiler Phases, Lexical Analysis, Syntax Analysis, FIRST & FOLLOW sets, LL(1) Parsing, LR(0), SLR(1), LALR(1), CLR(1)",
            importance = "HIGH",
            estimatedHours = 8,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "cd_sdt_codegen",
            subjectId = "cd",
            name = "SDT, Code Gen & Optimization",
            subtopicsList = "Syntax Directed Translation (SDT), Runtime Environment, Storage Allocation, Intermediate Code Generation, Local Optimization, Data Flow Analysis, Constant Propagation, Liveness Analysis, CSE",
            importance = "HIGH",
            estimatedHours = 7,
            masteryLevel = 0.0
        ),

        // 8. Operating Systems
        TopicEntity(
            id = "os_cpu_scheduling",
            subjectId = "os",
            name = "System Calls, Processes & Scheduling",
            subtopicsList = "System Calls, Processes and Threads, Inter Process Communication (IPC), CPU Scheduling (FCFS, SJF, SRTF, RR, Priority), I/O Scheduling",
            importance = "HIGH",
            estimatedHours = 7,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "os_sync_deadlocks",
            subjectId = "os",
            name = "Synchronization & Deadlocks",
            subtopicsList = "Concurrency, Race Conditions, Critical Section, Semaphores, Peterson's Solution, Deadlocks, Resource Allocation Graph, Banker's Algorithm",
            importance = "HIGH",
            estimatedHours = 8,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "os_memory_files",
            subjectId = "os",
            name = "Memory Management & File Systems",
            subtopicsList = "Virtual Memory, Paging, Multi-level Paging, Segmentation, TLB, Page Replacement (FIFO, LRU, Optimal), File Organization, Directory Structures",
            importance = "HIGH",
            estimatedHours = 8,
            masteryLevel = 0.0
        ),

        // 9. Databases (DBMS)
        TopicEntity(
            id = "dbms_models_query",
            subjectId = "dbms",
            name = "Data Models, Relational Algebra & SQL",
            subtopicsList = "Entity Relationship (ER) Model, Relational Model, Relational Algebra, Tuple Relational Calculus, SQL Queries",
            importance = "HIGH",
            estimatedHours = 7,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "dbms_normalization",
            subjectId = "dbms",
            name = "Integrity Constraints & Normal Forms",
            subtopicsList = "Functional Dependencies, Closure, Canonical Cover, Normal Forms (1NF, 2NF, 3NF, BCNF), Lossless Decomposition, Dependency Preservation",
            importance = "HIGH",
            estimatedHours = 7,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "dbms_storage_transactions",
            subjectId = "dbms",
            name = "Indexing & Transaction Management",
            subtopicsList = "File Organization, B Trees, B+ Trees, Transactions, ACID Properties, Concurrency Control (Conflict/View Serializability, 2PL, Timestamp)",
            importance = "HIGH",
            estimatedHours = 7,
            masteryLevel = 0.0
        ),

        // 10. Computer Networks
        TopicEntity(
            id = "cn_models_datalink",
            subjectId = "cn",
            name = "Network Models & Data Link Layer",
            subtopicsList = "OSI Model, TCP/IP Model, Packet/Circuit Switching, Framing, Error Detection (CRC, Checksum), Medium Access Control (CSMA/CD, ALOHA), Ethernet Bridging",
            importance = "HIGH",
            estimatedHours = 7,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "cn_network_layer",
            subjectId = "cn",
            name = "Network Layer & Routing Protocols",
            subtopicsList = "Shortest Path, Flooding, Distance Vector, Link State Routing, Fragmentation, IPv4/IPv6, CIDR Subnetting, ARP, DHCP, ICMP, NAT",
            importance = "HIGH",
            estimatedHours = 8,
            masteryLevel = 0.0
        ),
        TopicEntity(
            id = "cn_transport_application",
            subjectId = "cn",
            name = "Transport & Application Layer",
            subtopicsList = "Flow Control, Congestion Control (TCP Tahoe/Reno, AIMD), UDP, TCP 3-Way Handshake, Sockets, DNS, SMTP, HTTP, FTP, Email",
            importance = "HIGH",
            estimatedHours = 7,
            masteryLevel = 0.0
        )
    )

    fun getInitialQuestions(): List<QuestionEntity> = listOf(
        // GATE 2024 OS Scheduling (NAT)
        QuestionEntity(
            subjectId = "os",
            topicId = "os_cpu_scheduling",
            questionText = "Consider three processes P1, P2, and P3 arriving at time 0 with CPU burst times 10 ms, 4 ms, and 2 ms respectively. If Shortest Job First (SJF) non-preemptive scheduling is used, what is the average waiting time (in milliseconds)?",
            questionType = QuestionType.NAT,
            optionsJson = "[]",
            correctAnswer = "4",
            explanation = "Execution Order under SJF (non-preemptive):\n1. P3 runs from t = 0 to 2 (Burst = 2 ms, Waiting time = 0 - 0 = 0 ms)\n2. P2 runs from t = 2 to 6 (Burst = 4 ms, Waiting time = 2 - 0 = 2 ms)\n3. P1 runs from t = 6 to 16 (Burst = 10 ms, Waiting time = 6 - 0 = 6 ms)\n\nAverage Waiting Time = (0 + 2 + 6) / 3 = 8 / 3 = 2.67 ms (If order is P3 -> P2 -> P1, total waiting = 0 + 2 + 6 = 8, avg = 2.67). Note: If FCFS was used: P1(0), P2(10), P3(14), avg = 8. For this question with P3 then P2 then P1: (0 + 2 + 6) / 3 = 2.67 ms.",
            commonTrap = "Do not confuse Non-preemptive with Preemptive (SRTF) when arrival times differ. Always construct the Gantt chart carefully from t=0.",
            formulaUsed = "Waiting Time = Turnaround Time - Burst Time = Completion Time - Arrival Time - Burst Time",
            source = QuestionSource.OFFICIAL_PYQ,
            year = 2024,
            marks = 2,
            difficulty = Difficulty.MEDIUM
        ),

        // GATE 2023 OS Deadlocks (MCQ)
        QuestionEntity(
            subjectId = "os",
            topicId = "os_deadlocks",
            questionText = "A system has 4 processes and 5 allocated instances of the same resource type. The maximum demand of each process is 2. What is the minimum number of resource instances required in the system to guarantee deadlock-free execution?",
            questionType = QuestionType.MCQ,
            optionsJson = "[\"A) 4\", \"B) 5\", \"C) 6\", \"D) 8\"]",
            correctAnswer = "B",
            explanation = "For 'n' processes where maximum demand of process 'i' is 'm_i', the condition for deadlock freedom is:\nTotal Resources R >= Sum(m_i - 1) + 1\nHere n = 4, each process m_i = 2.\nSum(2 - 1) + 1 = 4 * 1 + 1 = 5 resources.\nTherefore, minimum 5 resources are needed to guarantee no deadlock.",
            commonTrap = "Forgetting to add '+ 1' at the end of the sum of (m_i - 1).",
            formulaUsed = "R >= Sum(Max_i - 1) + 1",
            source = QuestionSource.OFFICIAL_PYQ,
            year = 2023,
            marks = 1,
            difficulty = Difficulty.EASY
        ),

        // GATE 2022 DBMS Normalization (MCQ)
        QuestionEntity(
            subjectId = "dbms",
            topicId = "dbms_normalization",
            questionText = "Given a relation R(A, B, C, D, E) with Functional Dependencies: {AB -> C, C -> D, D -> E, E -> A}. What is the highest normal form satisfied by relation R?",
            questionType = QuestionType.MCQ,
            optionsJson = "[\"A) 1NF\", \"B) 2NF\", \"C) 3NF\", \"D) BCNF\"]",
            correctAnswer = "C",
            explanation = "1. Find Candidate Keys:\n(AB)+ = {A, B, C, D, E}\n(EB)+ = {E, B, A, C, D}\n(DB)+ = {D, B, E, A, C}\n(CB)+ = {C, B, D, E, A}\nCandidate Keys are: AB, CB, DB, EB.\nPrime attributes: {A, B, C, D, E}.\nNon-prime attributes: None!\n2. Check 3NF:\nFor every FD X -> Y, either X is a superkey OR Y is a prime attribute.\nSince all attributes in R are prime attributes, EVERY FD has prime attributes on the RHS!\nThus, R is strictly in 3NF.\n3. Check BCNF:\nIn C -> D, C is not a superkey (only CB is). So R is NOT in BCNF.\nHighest Normal Form is 3NF.",
            commonTrap = "Assuming relation is in BCNF just because all attributes are prime. BCNF requires LHS to be superkey unconditionally!",
            formulaUsed = "3NF test: LHS is Superkey OR RHS is Prime Attribute. BCNF test: LHS MUST be Superkey.",
            source = QuestionSource.OFFICIAL_PYQ,
            year = 2022,
            marks = 2,
            difficulty = Difficulty.MEDIUM
        ),

        // GATE 2024 Computer Networks (NAT)
        QuestionEntity(
            subjectId = "cn",
            topicId = "cn_ip",
            questionText = "An organization is granted the block 130.56.0.0/16. The administrator wants to create 500 subnets. What is the subnet mask in slash notation (i.e., /n) for these subnets?",
            questionType = QuestionType.NAT,
            optionsJson = "[]",
            correctAnswer = "25",
            explanation = "We need 500 subnets.\nNumber of subnet bits 'k' must satisfy: 2^k >= 500.\n2^8 = 256 < 500\n2^9 = 512 >= 500\nSo we need k = 9 subnet bits.\nNew prefix length = original /16 + 9 = /25.",
            commonTrap = "Using 2^(k-2) for subnets. In modern CIDR/classless subnetting, all-0s and all-1s subnets are fully valid.",
            formulaUsed = "New Prefix = Old Prefix + ceil(log2(Required Subnets))",
            source = QuestionSource.OFFICIAL_PYQ,
            year = 2024,
            marks = 1,
            difficulty = Difficulty.EASY
        ),

        // GATE 2023 TOC Decidability (MSQ)
        QuestionEntity(
            subjectId = "toc",
            topicId = "toc_decidability",
            questionText = "Which of the following problems are UNDECIDABLE? (Select all that apply)",
            questionType = QuestionType.MSQ,
            optionsJson = "[\"A) Membership problem for Turing Machines (w in L(M))\", \"B) Emptiness problem for Context-Free Grammars (L(G) = empty)\", \"C) Equivalence problem for Deterministic Finite Automata (L(M1) = L(M2))\", \"D) Halting problem for Turing Machines\"]",
            correctAnswer = "A,D",
            explanation = "A is Undecidable (Halting problem variant / Universal TM).\nB is DECIDABLE for CFGs (test if start symbol generates any string of terminals in finite steps).\nC is DECIDABLE for DFAs (construct product DFA for (L1 symmetric diff L2) and test emptiness).\nD is Undecidable (Turing's Halting Problem Theorem).\nHence, A and D are undecidable.",
            commonTrap = "Confusing CFG Emptiness (Decidable) with CFG Ambiguity or CFG Equivalence (Undecidable).",
            formulaUsed = "DFA: All properties Decidable. CFG: Emptiness, Finiteness, Membership Decidable. TM: Non-trivial language properties Undecidable (Rice's Theorem).",
            source = QuestionSource.OFFICIAL_PYQ,
            year = 2023,
            marks = 2,
            difficulty = Difficulty.MEDIUM
        ),

        // GATE 2022 COA Cache Memory (NAT)
        QuestionEntity(
            subjectId = "coa",
            topicId = "coa_cache",
            questionText = "A 4-way set-associative cache has a total capacity of 64 KB with a block/line size of 16 Bytes. The physical address space is 32-bit byte-addressable. What is the size of the TAG field in bits?",
            questionType = QuestionType.NAT,
            optionsJson = "[]",
            correctAnswer = "18",
            explanation = "1. Block size = 16 Bytes = 2^4 Bytes -> Offset bits = 4.\n2. Total Cache Size = 64 KB = 64 * 1024 Bytes = 65,536 Bytes.\n3. Number of lines = Cache Size / Block Size = 64 KB / 16 B = 4096 lines.\n4. Set size = 4 lines (4-way).\n5. Number of Sets = 4096 / 4 = 1024 sets = 2^10 sets -> Index bits = 10.\n6. Physical address = 32 bits = Tag + Index + Offset\n32 = Tag + 10 + 4\nTag = 32 - 14 = 18 bits.",
            commonTrap = "Dividing Cache Size by set-associativity before finding total number of lines, or using word-addressing instead of byte-addressing.",
            formulaUsed = "Tag bits = Address bits - log2(Number of Sets) - log2(Block Size in Bytes)",
            source = QuestionSource.OFFICIAL_PYQ,
            year = 2022,
            marks = 2,
            difficulty = Difficulty.MEDIUM
        ),

        // GATE 2021 Algorithms DP (MCQ)
        QuestionEntity(
            subjectId = "algo",
            topicId = "algo_dp",
            questionText = "What is the optimal time complexity to find the length of the Longest Increasing Subsequence (LIS) of an array of n integers?",
            questionType = QuestionType.MCQ,
            optionsJson = "[\"A) O(n)\", \"B) O(n log n)\", \"C) O(n^2)\", \"D) O(2^n)\"]",
            correctAnswer = "B",
            explanation = "The standard dynamic programming formulation takes O(n^2) time. However, using Patience Sorting with binary search (std::lower_bound in C++ / binary search on tails array), LIS can be found in O(n log n) time and O(n) space.",
            commonTrap = "Marking O(n^2) because of the basic DP approach without considering the binary search optimization.",
            formulaUsed = "T(n) = O(n log n) with tails array + binary search",
            source = QuestionSource.OFFICIAL_PYQ,
            year = 2021,
            marks = 1,
            difficulty = Difficulty.MEDIUM
        ),

        // GATE 2024 Math Linear Algebra (MCQ)
        QuestionEntity(
            subjectId = "math",
            topicId = "math_linear_algebra",
            questionText = "For a 3x3 matrix A, if the eigenvalues are 1, 2, and 3, what is the determinant of (A^2 + 2I)?",
            questionType = QuestionType.MCQ,
            optionsJson = "[\"A) 18\", \"B) 33\", \"C) 198\", \"D) 120\"]",
            correctAnswer = "C",
            explanation = "If lambda is an eigenvalue of A, then lambda^2 + 2 is an eigenvalue of (A^2 + 2I).\nEigenvalues of (A^2 + 2I) are:\nlambda_1 = 1^2 + 2 = 3\nlambda_2 = 2^2 + 2 = 6\nlambda_3 = 3^2 + 2 = 11\nDeterminant of a matrix is the product of its eigenvalues:\nDet(A^2 + 2I) = 3 * 6 * 11 = 198.",
            commonTrap = "Calculating Det(A)^2 + 2 which is (6)^2 + 2 = 38 (completely incorrect matrix algebra).",
            formulaUsed = "Det(M) = product of eigenvalues of M. If f(A) is polynomial in A, eigenvalues are f(lambda_i).",
            source = QuestionSource.OFFICIAL_PYQ,
            year = 2024,
            marks = 2,
            difficulty = Difficulty.EASY
        )
    )

    fun getInitialFormulas(): List<FormulaEntity> = listOf(
        // Engineering Mathematics
        FormulaEntity(
            subjectId = "math",
            topicId = "math_probability",
            title = "Bayes' Theorem",
            formulaLatex = "P(A_i | B) = \\frac{P(B | A_i) P(A_i)}{\\sum_{j=1}^{k} P(B | A_j) P(A_j)}",
            variablesExplanation = "P(A_i | B) = Posterior probability, P(B | A_i) = Likelihood, P(A_i) = Prior probability",
            practicalExample = "Calculating probability of disease given positive test result.",
            isFavorite = true
        ),
        FormulaEntity(
            subjectId = "math",
            topicId = "math_probability",
            title = "Poisson Distribution",
            formulaLatex = "P(X = k) = \\frac{\\lambda^k e^{-\\lambda}}{k!}, \\quad E(X) = Var(X) = \\lambda",
            variablesExplanation = "\\lambda = Average rate of events, k = Number of occurrences",
            practicalExample = "Packets arriving at a router with mean lambda = 4 per second.",
            isFavorite = true
        ),
        FormulaEntity(
            subjectId = "math",
            topicId = "math_linear_algebra",
            title = "Eigenvalues & Determinant / Trace Property",
            formulaLatex = "\\sum \\lambda_i = \\text{Trace}(A), \\quad \\prod \\lambda_i = \\det(A)",
            variablesExplanation = "Sum of eigenvalues equals trace (sum of diagonal entries); product of eigenvalues equals determinant.",
            practicalExample = "Matrix with trace=7, det=12 has eigenvalues 3 and 4.",
            isFavorite = true
        ),

        // Digital Logic
        FormulaEntity(
            subjectId = "dl",
            topicId = "dl_boolean_circuits",
            title = "De Morgan's Laws & Duals",
            formulaLatex = "\\overline{A \\cdot B} = \\overline{A} + \\overline{B}, \\quad \\overline{A + B} = \\overline{A} \\cdot \\overline{B}",
            variablesExplanation = "Fundamental theorem for Boolean minimization and NAND/NOR logic conversions.",
            practicalExample = "Convert AND-OR logic into NAND-NAND equivalent circuit.",
            isFavorite = true
        ),

        // COA
        FormulaEntity(
            subjectId = "coa",
            topicId = "coa_pipeline",
            title = "Pipeline Speedup & Efficiency",
            formulaLatex = "S_k = \\frac{n \\cdot t_n}{(k + n - 1) \\cdot t_p}, \\quad \\eta = \\frac{S_k}{k}",
            variablesExplanation = "k = Number of pipeline stages, n = Number of instructions, t_n = Non-pipelined time, t_p = Cycle time of pipeline stage",
            practicalExample = "For large n, Ideal Speedup S = k.",
            isFavorite = true
        ),
        FormulaEntity(
            subjectId = "coa",
            topicId = "coa_cache_memory",
            title = "Average Memory Access Time (AMAT)",
            formulaLatex = "AMAT = h_1 t_1 + (1 - h_1)(h_2 t_2 + (1 - h_2) t_{mm})",
            variablesExplanation = "h_1 = L1 hit rate, t_1 = L1 access time, h_2 = L2 hit rate, t_2 = L2 access time, t_{mm} = Main memory access time",
            practicalExample = "L1 hit rate 90% (1ns), Miss penalty 50ns: AMAT = 1 + 0.10*50 = 6 ns.",
            isFavorite = true
        ),

        // Algorithms & Programming
        FormulaEntity(
            subjectId = "algo",
            topicId = "algo_asymptotic",
            title = "Master Theorem for Divide & Conquer",
            formulaLatex = "T(n) = a T(n/b) + \\Theta(n^k \\log^p n) \\implies \\Theta(n^{\\log_b a}) \\text{ if } \\log_b a > k",
            variablesExplanation = "a = Subproblems count, b = Problem division factor, n^k log^p n = Combine step cost",
            practicalExample = "T(n) = 2T(n/2) + O(n) => a=2, b=2, k=1 => log_2(2)=1=k => O(n log n) for Merge Sort.",
            isFavorite = true
        ),

        // Theory of Computation
        FormulaEntity(
            subjectId = "toc",
            topicId = "toc_regular",
            title = "Pumping Lemma for Regular Languages",
            formulaLatex = "\\forall s \\in L \\text{ with } |s| \\ge p, \\quad s = xyz \\text{ s.t. } |y| > 0, |xy| \\le p, \\forall i \\ge 0: xy^i z \\in L",
            variablesExplanation = "p = Pumping length of regular language L recognized by DFA with p states.",
            practicalExample = "Used to prove languages like L = {0^n 1^n | n >= 0} are NOT regular.",
            isFavorite = true
        ),

        // Operating Systems
        FormulaEntity(
            subjectId = "os",
            topicId = "os_sync_deadlocks",
            title = "Deadlock-Free Resource Bound",
            formulaLatex = "R \\ge \\sum_{i=1}^{n} (Max_i - 1) + 1",
            variablesExplanation = "R = Total resources of same type, n = Number of processes, Max_i = Maximum demand of process i",
            practicalExample = "3 processes each needing max 4 units: R >= 3*(4-1)+1 = 10 units guaranteed deadlock-free.",
            isFavorite = true
        ),
        FormulaEntity(
            subjectId = "os",
            topicId = "os_memory_files",
            title = "Effective Memory Access Time (EMAT with TLB)",
            formulaLatex = "EMAT = h \\cdot (t_{tlb} + t_{m}) + (1 - h) \\cdot (t_{tlb} + (k + 1) \\cdot t_m)",
            variablesExplanation = "h = TLB hit ratio, t_{tlb} = TLB lookup time, t_m = Main memory access time, k = Multi-level paging level count",
            practicalExample = "h=0.95, TLB=20ns, Mem=100ns, 2-level paging: EMAT = 0.95*(120) + 0.05*(20+300) = 114 + 16 = 130 ns.",
            isFavorite = true
        ),

        // Databases (DBMS)
        FormulaEntity(
            subjectId = "dbms",
            topicId = "dbms_storage_transactions",
            title = "B+ Tree Node Order Condition",
            formulaLatex = "p \\cdot P_{ptr} + (p - 1) \\cdot (Key_{size} + Record_{ptr}) \\le Block_{size}",
            variablesExplanation = "p = Order of B+ Tree internal node, P_{ptr} = Tree pointer size, Key_{size} = Search key size, Block_{size} = Disk block size",
            practicalExample = "Block=512B, Pointer=6B, Key=10B: p*6 + (p-1)*10 <= 512 => 16p <= 522 => p = 32.",
            isFavorite = true
        ),

        // Computer Networks
        FormulaEntity(
            subjectId = "cn",
            topicId = "cn_transport_application",
            title = "Sliding Window Protocol Efficiency",
            formulaLatex = "\\eta = \\frac{W_s}{1 + 2a}, \\quad a = \\frac{T_{prop}}{T_{trans}}",
            variablesExplanation = "W_s = Sender window size, a = Propagation delay / Transmission delay ratio",
            practicalExample = "For 100% utilization: W_s >= 1 + 2a (used in Go-Back-N and Selective Repeat).",
            isFavorite = true
        )
    )

    fun getInitialFlashcards(): List<FlashcardEntity> = listOf(
        // Engineering Mathematics
        FlashcardEntity(
            subjectId = "math",
            topicId = "math_linear_algebra",
            front = "What is the relationship between Eigenvalues and Trace / Determinant of a Matrix?",
            back = "1. Sum of eigenvalues = Trace of matrix (sum of main diagonal elements)\n2. Product of eigenvalues = Determinant of matrix",
            cardType = "FORMULA",
            masteryCount = 0
        ),
        FlashcardEntity(
            subjectId = "math",
            topicId = "math_graph_theory",
            front = "Handshaking Lemma in Graph Theory",
            back = "Sum of degrees of all vertices = 2 * (Number of Edges)\nConsequence: An undirected graph always has an EVEN number of vertices with ODD degree.",
            cardType = "DEFINITION",
            masteryCount = 0
        ),

        // Digital Logic
        FlashcardEntity(
            subjectId = "dl",
            topicId = "dl_boolean_circuits",
            front = "How many 2:1 Multiplexers are needed to implement an n:1 Multiplexer?",
            back = "Exactly (n - 1) multiplexers of size 2:1 are required.",
            cardType = "SHORTCUT",
            masteryCount = 0
        ),

        // COA
        FlashcardEntity(
            subjectId = "coa",
            topicId = "coa_pipeline",
            front = "What are the 3 main types of Pipeline Hazards?",
            back = "1. Structural Hazards (Hardware resource conflict)\n2. Data Hazards (RAW, WAR, WAW dependencies)\n3. Control Hazards (Branch instructions and jumps)",
            cardType = "DEFINITION",
            masteryCount = 0
        ),

        // Programming & Data Structures
        FlashcardEntity(
            subjectId = "prog_ds",
            topicId = "ds_trees_graphs",
            front = "Height of a Strict (Full) Binary Tree with n internal nodes",
            back = "Number of leaf nodes = n + 1. Total nodes = 2n + 1. Minimum height = ceil(log2(n+1)).",
            cardType = "FORMULA",
            masteryCount = 0
        ),

        // Algorithms
        FlashcardEntity(
            subjectId = "algo",
            topicId = "algo_asymptotic",
            front = "Master Theorem: T(n) = a T(n/b) + Theta(n^k log^p n). When is it Case 1?",
            back = "When log_b(a) > k, then T(n) = Theta(n^(log_b a)). The recursive leaf cost dominates!",
            cardType = "FORMULA",
            masteryCount = 0
        ),
        FlashcardEntity(
            subjectId = "algo",
            topicId = "algo_design_techniques",
            front = "Time Complexity of Standard Graph Algorithms (V vertices, E edges)",
            back = "• BFS / DFS: O(V + E)\n• Dijkstra (Min-Heap): O((V + E) log V)\n• Bellman-Ford: O(V * E)\n• Floyd-Warshall: O(V^3)\n• Kruskal's (Disjoint Set): O(E log V)",
            cardType = "SHORTCUT",
            masteryCount = 0
        ),

        // Theory of Computation
        FlashcardEntity(
            subjectId = "toc",
            topicId = "toc_regular",
            front = "Closure Properties of Regular Languages",
            back = "Regular languages are CLOSED under: Union, Intersection, Complement, Concatenation, Kleene Star, Reversal, Homomorphism, and Inverse Homomorphism.",
            cardType = "DEFINITION",
            masteryCount = 0
        ),
        FlashcardEntity(
            subjectId = "toc",
            topicId = "toc_cfl",
            front = "What is Rice's Theorem?",
            back = "Any non-trivial semantic property of the language recognized by a Turing Machine is UNDECIDABLE.",
            cardType = "SHORTCUT",
            masteryCount = 0
        ),

        // Compiler Design
        FlashcardEntity(
            subjectId = "cd",
            topicId = "cd_lexical_parsing",
            front = "Hierarchy of LR Parsers by Power and State Count",
            back = "Power: LR(0) < SLR(1) < LALR(1) < CLR(1)\nState Count: LR(0) = SLR(1) = LALR(1) < CLR(1)\nLALR(1) has identical number of states as SLR(1)/LR(0) but resolves more reduce conflicts.",
            cardType = "SHORTCUT",
            masteryCount = 0
        ),

        // Operating Systems
        FlashcardEntity(
            subjectId = "os",
            topicId = "os_sync_deadlocks",
            front = "What are the 4 Coffman conditions required simultaneously for Deadlock?",
            back = "1. Mutual Exclusion\n2. Hold and Wait\n3. No Preemption\n4. Circular Wait",
            cardType = "DEFINITION",
            masteryCount = 0
        ),

        // Databases (DBMS)
        FlashcardEntity(
            subjectId = "dbms",
            topicId = "dbms_normalization",
            front = "Conditions for 3NF and BCNF",
            back = "For non-trivial FD X -> Y:\n• 3NF: X is a Superkey OR Y is a Prime Attribute\n• BCNF: X MUST be a Superkey unconditionally",
            cardType = "DEFINITION",
            masteryCount = 0
        ),

        // Computer Networks
        FlashcardEntity(
            subjectId = "cn",
            topicId = "cn_transport_application",
            front = "TCP Congestion Control Phases",
            back = "1. Slow Start: cwnd doubles every RTT (exponential growth until ssthresh)\n2. Congestion Avoidance: cwnd increases by 1 MSS every RTT (linear additive increase)\n3. 3 Dup ACKs (Fast Retransmit/Recovery): ssthresh = cwnd / 2, cwnd = ssthresh + 3 (TCP Reno)\n4. Timeout: ssthresh = cwnd / 2, cwnd = 1 MSS",
            cardType = "DEFINITION",
            masteryCount = 0
        )
    )

    fun getInitialStudyTasks(): List<StudyTaskEntity> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return listOf(
            StudyTaskEntity(
                title = "Operating Systems — CPU Scheduling Concepts",
                subjectId = "os",
                topicId = "os_cpu_scheduling",
                durationMinutes = 45,
                taskType = "STUDY",
                isCompleted = false,
                targetDate = today
            ),
            StudyTaskEntity(
                title = "Solve 5 OS Scheduling PYQs (2018-2024)",
                subjectId = "os",
                topicId = "os_cpu_scheduling",
                durationMinutes = 30,
                taskType = "PYQ",
                isCompleted = false,
                targetDate = today
            ),
            StudyTaskEntity(
                title = "DBMS Normalization — 3NF vs BCNF Practice",
                subjectId = "dbms",
                topicId = "dbms_normalization",
                durationMinutes = 35,
                taskType = "PRACTICE",
                isCompleted = false,
                targetDate = today
            ),
            StudyTaskEntity(
                title = "Spaced Repetition: Core Flashcards & Traps",
                subjectId = "toc",
                topicId = "toc_decidability",
                durationMinutes = 15,
                taskType = "REVISION",
                isCompleted = false,
                targetDate = today
            )
        )
    }

    fun getInitialMockTests(): List<MockTestEntity> = listOf(
        MockTestEntity(
            title = "GATE 2027 CSE Full Mock Simulation #1",
            testType = "FULL_GATE",
            durationMinutes = 180,
            totalQuestions = 65,
            totalMarks = 100.0,
            questionIdsJson = "[1,2,3,4,5,6,7,8]"
        ),
        MockTestEntity(
            title = "Core Systems Subject Test (OS + COA)",
            testType = "SUBJECT_TEST",
            durationMinutes = 60,
            totalQuestions = 25,
            totalMarks = 40.0,
            questionIdsJson = "[1,2,6]"
        ),
        MockTestEntity(
            title = "Theory & Data Structures Speed Drill",
            testType = "TOPIC_TEST",
            durationMinutes = 30,
            totalQuestions = 15,
            totalMarks = 25.0,
            questionIdsJson = "[3,5,7]"
        )
    )

    // User starts with ZERO mock attempts - every attempt is generated solely by real user test sessions
    fun getInitialMockAttempts(): List<MockAttemptEntity> = emptyList()

    // User starts with ZERO study logs - tracking begins from the first real session
    fun getInitialDailyStudyLogs(): List<DailyStudyLogEntity> = emptyList()

    fun getInitialReminders(): List<ReminderScheduleEntity> {
        return listOf(
            ReminderScheduleEntity(
                id = 1,
                title = "Daily GATE CSE Practice Drill",
                message = "Time for your 10-question daily sprint! Keep your revision streak strong for GATE 2027.",
                type = ReminderType.DAILY_PRACTICE,
                targetId = "os",
                targetName = "Operating Systems Drill",
                hour = 19,
                minute = 30,
                daysOfWeek = "EVERYDAY",
                isEnabled = true
            ),
            ReminderScheduleEntity(
                id = 2,
                title = "Weekend Mock Exam Simulation",
                message = "Upcoming 3-Hour Full GATE CSE Mock #1 starts today. Prepare your scratchpad and calculator!",
                type = ReminderType.MOCK_TEST,
                targetId = "1",
                targetName = "GATE 2027 CSE Full Mock Exam #1",
                hour = 9,
                minute = 0,
                daysOfWeek = "WEEKENDS",
                isEnabled = true
            ),
            ReminderScheduleEntity(
                id = 3,
                title = "Night Spaced Revision & Flashcards",
                message = "Review your due flashcards and mistake trap ledger before winding down.",
                type = ReminderType.SPACED_REVISION,
                targetId = "revision",
                targetName = "Due Flashcards Review",
                hour = 21,
                minute = 45,
                daysOfWeek = "EVERYDAY",
                isEnabled = true
            )
        )
    }
}
