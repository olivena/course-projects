## TODO List
1. Construct basic structure for program based on pseudocode, 
2. Define and construct algorithms based on pseudocode on paper, add helping functions
3. Implement time and memory tracking features for comparing running times,
4. Create usability actions fo user,
5. Test functions to see if they work as should, create test inputs
6. Check that the code is commented
(7. Implementation of multi-user model)

## Your Report
# What Functional Encryption (FE) is and how it works?
Functional Encryption is a public-key system where encrypted data can be accessed with limited secret keys, only accessing specified functions, meaning that we can do computations on encrypted data while maintaining privacy and security. With a secret key to a function, user gets access to a decrypted output, that was generated based on the ciphertext and key. An owner of secret key can never get access to the whole data that was originally encrypted. The limited secret keys used for computations are generated and distributed by authority with master key.

Functional encryption system has three different user roles which are data owners who are responsible of the data and encrypts it, key curators who generates decryption keys for users, and users who requests access to specified functions and gets limited access to the data.

Functional Encryption scheme is suitable for situations where analyzis or computations are performed on encrypted data without decrypting the whole material. It is useful in databases containing patient or other personal data, since using limited keys only gives out information regarding specified information. Or in cases where it is important to share private information to outsider, without revealing too much, for example about company's network.

# How AoS works?
Ace of SPADE (AoS) is a Functional Encryption scheme, that provides a way to perform qualitative analysis on decrypted data. AoS also enables multi-client setup, so that any user can access their own targeted computations. AoS extends FE to broader range of funcitonalities and allows qualitative analysis on the encrypted data, which is not possible in some other types of FE. AoS is combined of FE and Ring-Learning with errors (RLWE). RLWE is a computational problem that operates with polynomial rings [1]. In RLWE it should be computationally infeasible to recover secret key without enough information. RLWE is used as a basis for homomorphic encryption [1]. Since AoS relies on lattice-based cryptography, it is also quantum-safe, meaning it is resistant to attacks from quantum computers.

What is also remarkable about AoS is the possibility it offers on updating the encrypted data without re-encryption, which has previously not been possible.

## Final Results & Conclusion

I used Copilot Chat and github Copilot to understand the functions and mathematics used and to get suggestions on how to create them on the program in a secure way. I chose to program with python, but I believe using Go would have been more suitable. Go uses minimal memory and is faster to compile [2]. I chose Python since I'm more used to programming with it. 

I did not implement multi-user setting on the program.

The evaluation was conducted on PC with Intel Core Ultra 7 155H CPU @ 1.40 GHz and 32GB of memory. I tried using same parameters for benchmarking as in paper, but encountered an issue when using n = 2**12. Using even the parameterset S it takes a long time to go through the program. For this reason, I created a new set named Xsmall, which uses N half the size of set S N.

Set sizes and their values:

| Parameters|    m | log(2)N | log(2)q | log(2)t | 
|-----------|------|---------|---------|---------|
| Xsmall    |  100 |       6 |      39 |      16 |
| Small     |  100 |      12 |      39 |      16 |
| Medium    |  200 |      13 |      42 |      16 |
| Large     |  500 |      14 |      43 |      16 |
| XLarge    | 1000 |      15 |      47 |      16 |

B1 with parameter set XS:

| Component    | Time (ms/op) | Memory Allocation (KB/op) |
|--------------|--------------|---------------------------|
| Setup        |        937.5 |                  301.6875 |
| Encryption   |        31.25 |                   22.1992 |
| KeyDerivation|     2171.875 |                   434.125 |
| Decryption   |        812.5 |                  407.6875 |
| TokenGen     |     2047.875 |                  559.3047 |
| Update       |       15.625 |                   568.355 |

When comparing my results to the article's results, we can see that there is major differences in running times per operations as well as in memory allocations. My programs functions take a lot more time, but use a lot less memory. I also had to use smaller parameter set for running the program, since it took so much time to run with bigger polynomial ring degrees. 

There is also a lot of varying in time usage when run with the same set.

# Issues and future improvements
This chapter is made mainly for myself to reflect on how the project went, and what I would like to improve about it in the future. During this project I encountered a lot of issues varying from understanding the protocols and functions, to issues with programming. I learned a lot more about programming with Artificial Intelligence tools, mainly about proofreading and understandig better how to use it. 

Issues still in the program:
- encrypts currently only part of the plaintext - rest of the ciphertext does not exist (might be because of used n size)
- using correct sized N makes to program use a lot of time, even with sample S
- decryption does not work correctly, due to issues with variables

Future improvements
- separation of programs for different uses (Data owners, key curation, users)
- implementing hashing, if needed other more secure ways of communicating with the different programs
- test the program more, 

Used resources:
[1] https://en.wikipedia.org/wiki/Ring_learning_with_errors
[2] https://mobilunity.com/blog/golang-vs-python/