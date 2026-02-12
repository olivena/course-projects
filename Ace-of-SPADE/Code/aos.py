import secrets        #provides cryptographic randomness
import math
import time           #to get time per operation
import tracemalloc    #for memory allocation tracking
import pickle         #for saving encrypted data to a file

#Helper: Select parameter set
def select_param_set(name):
    ''' 
    Sets are defined as [n, m, q, X_sk, X_e]
    where n = polynomial ring degree, 
    m = input vector length, 
    q = ciphertext modulus, 
    X_sk = secret,
    X_e = error,
    t = plaintext modulus
    '''
    if name == 'xs':
        return [2**6, 100, 2**39, 0, 0]
    elif name == 's':
        return [2**12, 100, 2**39, 0, 0]
    elif name == 'm':
        return [2**13, 200, 2**42, 0, 0]
    elif name == 'l':
        return [2**14, 500, 2**43, 0, 0]
    elif name == 'xl':
        return [2**15, 1000, 2**47, 0, 0]
    else:
        raise ValueError("Unknown parameter set name")

#Helper: Preprocess plaintext string into binary
def preprocess_plaintext(plaintext, n, q):
    #Convert plaintext to binary
    binary = ''.join(format(ord(char), '08b') for char in plaintext)
    
    #Map binary to polynomial coefficients
    coefficients = [int(bit) for bit in binary]
       
    #Pad or truncate to fit degree n-1
    if len(coefficients) > n:
        coefficients = coefficients[:n]
    else:
        coefficients += [0] * (n - len(coefficients))
    
    #Reduce coefficients modulo q
    coefficients = [coef % q for coef in coefficients]
    return coefficients

#Helper: Sample ternary polynomial (coefficients in {-1,0,1}) using cryptographic randomness
def sample_ternary_poly(size):
    #Map random bytes to -1, 0, 1
    result = []
    for _ in range(size):
        b = secrets.randbelow(3)
        result.append([-1, 0, 1][b])
    return result

#Helper: Sample uniformly random polynomial in Z_q[X]/(X^n+1) using cryptographic randomness
def sample_uniform_poly(size, q):
    return [secrets.randbelow(q) for _ in range(size)]

#Helper: Sample error from discrete Gaussian (approximate with Box-Muller using cryptographic randomness)
def sample_gaussian_poly(size, sigma):
    """
    Sample a polynomial with coefficients drawn from a discrete Gaussian distribution.
    Uses the Box-Muller transform with cryptographic randomness.
    """
    result = []
    for _ in range(size):
        #Generate two uniform random floats in (0,1]
        u1 = secrets.randbelow(2**53) / (2**53)  #Generates a random float in (0,1)
        u2 = secrets.randbelow(2**53) / (2**53)  #Generates another random float in (0,1)
        z0 = math.sqrt(-2.0 * math.log(u1)) * math.cos(2 * math.pi * u2)
        val = int(round(z0 * sigma))
        result.append(val)
    return result

#Helper: Polynomial helper functions (coefficient-wise operations modulo q)
def poly_mod(p, q):
    return [int(coef) % q for coef in p]

#Helper: Polynomial addition, subtraction, multiplication in R_q = Z_q[X]/(X^n+1)
def poly_add(p, r, q):
    """
    Add two polynomials p and r modulo q.
    If the lengths of p and r differ, the shorter polynomial is padded with zeros.
    """
    if isinstance(p, int):
        p = [p]
    if isinstance(r, int):
        r = [r]

    max_len = max(len(p), len(r))
    p = (p + [0] * max_len)[:max_len]
    r = (r + [0] * max_len)[:max_len]
    return [(int(p[i]) + int(r[i])) % q for i in range(max_len)]

#Helper: Polynomial subtraction
def poly_sub(p, r, q):
    """
    Subtract polynomial r from polynomial p modulo q.
    If the lengths of p and r differ, the shorter polynomial is padded with zeros.
    """
    max_len = max(len(p), len(r))
    p = (p + [0] * max_len)[:max_len]
    r = (r + [0] * max_len)[:max_len]
    return [(int(p[i]) - int(r[i])) % q for i in range(max_len)]

#Helper: Polynomial coefficient-wise multiplication
def poly_hadamard(p, r, q):
    #coefficient-wise multiplication (used as a simple ring-product approximation)
    if isinstance(r, int):
        r = [r] * len(p)
    return [ (int(p[i]) * int(r[i])) % q for i in range(len(p)) ]

#Helper: Polynomial multiplication in R_q = Z_q[X]/(X^n+1)
def poly_mul(p, r, q):
    """
    Negacyclic convolution modulo X^n + 1.
    Computes product of two polynomials p and r (lists of length n) in R_q = Z_q[X]/(X^n+1).
    If the lengths of p and r differ, the shorter polynomial is padded with zeros.
    """
    if isinstance(p, int):
        p = [p]
    if isinstance(r, int):
        r = [r]

    #Ensure both polynomials have the same length by padding with zeros
    if len(p) != len(r):
        max_len = max(len(p), len(r))
        p = (p + [0] * max_len)[:max_len]
        r = (r + [0] * max_len)[:max_len]

    n = len(p)
    if len(r) != n:
        raise ValueError(f"Length mismatch after padding: len(p)={len(p)}, len(r)={len(r)}")

    res = [0] * n
    for i in range(n):
        for j in range(n):
            idx = (i + j) % n  #Wrap around using modulo n
            sign = -1 if (i + j) >= n else 1  #Negacyclic adjustment
            res[idx] += sign * p[i] * r[j]
    return [x % q for x in res]

#Helper: Polynomial scalar multiplication
def poly_scalar_mul(p, scalar, q):
    return [ (int(coef) * int(scalar)) % q for coef in p ]

#Setup algorithm, generates secret key (sk), public key (pk), and label key (k_alpha), which are used for, public key to encrypt, label key associated with alpha
def setup(pp):
    start_time = time.process_time() #starts time measurement
    tracemalloc.start() #starts memory allocation tracking

    n, m, q, X_sk, X_e = pp
    sigma = 2.0 

    #secret key
    sk = [sample_ternary_poly(n) for _ in range(m)]

    #public polynomial a
    a = sample_uniform_poly(n, q)

    #errors, RLWE
    e = [sample_gaussian_poly(n, sigma) for _ in range(m+1)]
    e0 = e[:-1]
    e_alpha = e[-1]

    #label key
    alpha = sample_ternary_poly(n)

    t = 2**16 #plaintext modulus, given in benchmark description

    #k_alpha = a * alpha + t * e_alpha (ring multiplication)
    k_alpha = poly_add(poly_mul(a, alpha, q), poly_scalar_mul(e_alpha, t, q), q)

    #Compute public key pk 
    pk = []
    for i in range(n):
        prod = poly_mul(a, sk[i], q)
        neg_prod = poly_scalar_mul(prod, -1, q)
        b_i = poly_add(neg_prod, poly_scalar_mul(e0[i], t, q), q)
        pk.append((b_i, a))

    end_time = time.process_time()
    elapsed_time = (end_time - start_time) * 1000 #milliseconds

    current, peak = tracemalloc.get_traced_memory()
    tracemalloc.stop()

    print("Setup:", elapsed_time, "ms, ", current / 1024, "KB")
    return (sk, pk, k_alpha)

#Encryption algorithm, run by data owner to encrypt message x under public key pk_i and label alpha, produced by key curator
def enc(pk_i, x, alpha, q, sigma):
    start_time = time.process_time()
    tracemalloc.start()

    b_i, a = pk_i 
    n = len(a)

    #Sample u uniformly in R_2 (ternary poly), e1, e2 from Gaussian
    u = sample_ternary_poly(n)
    e1 = sample_gaussian_poly(n, sigma)
    e2 = sample_gaussian_poly(n, sigma)

    t = 2**16

    #convert message x to polynomial of length n
    def to_poly(xval):
        if isinstance(xval, int):
            poly = [0] * n
            poly[0] = xval
            return poly
        if isinstance(xval, list):
            if len(xval) != n:
                return (xval + [0]*n)[:n]
            return xval
        raise ValueError('Unsupported message type for x')

    x_poly = to_poly(x)

    #c1 = b_i * alpha + t * e1 + u * x
    term1 = poly_mul(b_i, alpha, q)
    term2 = poly_scalar_mul(e1, t, q)
    term3 = poly_mul(u, x_poly, q)
    c1 = poly_add(poly_add(term1, term2, q), term3, q)

    #c2 = a * alpha + t * e2 - u
    term4 = poly_mul(a, alpha, q)
    term5 = poly_scalar_mul(e2, t, q)
    c2 = poly_sub(poly_add(term4, term5, q), u, q)

    end_time = time.process_time()
    elapsed_time = (end_time - start_time) * 1000  # milliseconds

    current, peak = tracemalloc.get_traced_memory()
    tracemalloc.stop()

    print("Encryption:", elapsed_time, "ms, ", current / 1024, "KB")

    with open("ciphertext.bin", "wb") as f:
        pickle.dump((c1, c2), f)

    return (c1, c2)

#Key derivation algorithm, to get decryption key dk associated with key_alpha and value v we want to see
def key_der(sk, k_alpha, v, q, sigma):
    start_time = time.process_time()
    tracemalloc.start()
    
    m = len(sk)
    #determine polynomial length n
    if m == 0:
        return []
    n = len(sk[0])

    #ensure v is a polynomial list
    if isinstance(v, int):
        v_poly = [v] * n
    else:
        v_poly = v

    e3 = sample_gaussian_poly(n, sigma)

    dk = []
    t = 2**16
    for i in range(m):
        diff = poly_sub(sk[i], v_poly, q)
        term = poly_mul(k_alpha, diff, q)
        err_term = poly_scalar_mul(e3, t, q)
        k_i = poly_add(term, err_term, q)
        dk.append(k_i)

    end_time = time.process_time()
    elapsed_time = (end_time - start_time) * 1000  # milliseconds

    current, peak = tracemalloc.get_traced_memory()
    tracemalloc.stop()

    print("KeyDerivation:", elapsed_time, "ms, ", current / 1024, "KB")
    return dk

#function to partially decrypt ciphertext, done by user
def dec(dk, c_x, v, q):
    time_start = time.process_time()
    tracemalloc.start()

    t = 2**16
    m = len(dk)
    if m == 0:
        return []

    #determine polynomial length n from first dk element
    n = len(dk[0])

    #prepare v as polynomial
    if isinstance(v, int):
        v_poly = [v] * n
    else:
        v_poly = v if len(v) == n else (v + [0]*n)[:n]

    y_out = []
    for i in range(m):
        c1 = c_x[0][i]  
        c2 = c_x[1][i]
        k_i = dk[i]

        #y* = c1 + k_i + v * c2  (all ring ops)
        yc = poly_add(poly_add(c1, k_i, q), poly_mul(v_poly, c2, q), q)

        #reduce coefficients modulo t into [0, t)
        y_mod_t = [int(coef) % t for coef in yc]

        #map each coefficient into centered representative in (-q/2, q/2]
        half_q = q // 2
        y_centered = []
        for a in y_mod_t:
            #a is in 0..t-1; lift into integer and center in (-q/2, q/2]
            if a > half_q:
                y_centered.append(a - q)
            else:
                y_centered.append(a)

        y_out.append(y_centered)

    time_end = time.process_time()
    elapsed_time = (time_end - time_start) * 1000  #milliseconds   

    current, peak = tracemalloc.get_traced_memory()
    tracemalloc.stop()

    print("Decryption:", elapsed_time, "ms, ", current / 1024, "KB")

    flat = [coef for poly in y_out for coef in poly]
    plaintext_bytes = bytes([coef % 256 for coef in flat])

    readable_text = plaintext_bytes.decode("utf-8", errors="ignore")
    with open("output.bin", "bw") as f:
        f.write(plaintext_bytes)

    return y_out

#Token generation for updating ciphertext
def tokgen(pk, alpha_in, alpha_out, q, sigma, t):
    time_start = time.process_time()
    tracemalloc.start()

    m = len(pk)
    if m == 0:
        return []
    
    #determine polynomial length n from a in pk[0]
    _, a0 = pk[0]
    n = len(a0)

    #delta = alpha_out - alpha_in (polynomial)
    if isinstance(alpha_in, int):
        alpha_in = [alpha_in] * n
    if isinstance(alpha_out, int):
        alpha_out = [alpha_out] * n
    delta = poly_sub(alpha_out, alpha_in, q)

    tk = []
    for i in range(m):
        b_i, a = pk[i]
        #sample two Gaussian error polys for token noise
        et1 = sample_gaussian_poly(n, sigma)
        et2 = sample_gaussian_poly(n, sigma)

        #tk1 = b_i * delta + t * et1
        tk1 = poly_add(poly_mul(b_i, delta, q), poly_scalar_mul(et1, t, q), q)
        #tk2 = a * delta + t * et2
        tk2 = poly_add(poly_mul(a, delta, q), poly_scalar_mul(et2, t, q), q)

        tk.append((tk1, tk2))

    time_end = time.process_time()
    elapsed_time = (time_end - time_start) * 1000  #milliseconds

    current, peak = tracemalloc.get_traced_memory()
    tracemalloc.stop()

    print("TokenGen:", elapsed_time, "ms, ", current / 1024, "KB")
    return tk

#Update algorithm to update ciphertexts
def upd(tk, c_x, q):
    time_start = time.process_time()
    tracemalloc.start()

    m = len(tk)
    if m == 0:
        return []
    c_out = []
    for i in range(m):
        tk1, tk2 = tk[i]
        c1 = c_x[0][i]  
        c2 = c_x[1][i]
        new_c1 = poly_add(c1, tk1, q)
        new_c2 = poly_add(c2, tk2, q)
        c_out.append((new_c1, new_c2))

    time_end = time.process_time()
    elapsed_time = (time_end - time_start) * 1000  #milliseconds

    current, peak = tracemalloc.get_traced_memory()
    tracemalloc.stop()

    print("Update:", elapsed_time, "ms, ", current / 1024, "KB")

    return c_out

while(True):
    t = 2**16
    request = input("Enter command 's' to start or 'q' to quit: ")

    if request == 'q':
        break
    elif request == 's':
        size = input("Enter parameter set (xs, s, m, l, xl): ")
        set = select_param_set(size)
        
        keys = setup(set)
        sk, pk, k_alpha = keys

        file_to_encrypt = input("Enter file to encrypt (example.txt): ")
        
        with open(file_to_encrypt, 'r') as file:
            x = file.read() 
        x = preprocess_plaintext(x, set[1], set[2]) #text to binary

        encrypted = enc(pk[1], x, sample_ternary_poly(set[1]), set[2], 2.0) #pk_i, x, alpha, q, sigma
        c1, c2 = encrypted

        #not sure in what format v should be provided if it would be a plaintext like "Cloud"
        v = int(input("Enter a value v to derive key for (integer): "))
        key_derived = key_der(sk, k_alpha, v, set[2], 2.0)

        file_to_decrypt = input("Enter file to save ciphertext (ciphertext.bin): ")
        with open(file_to_decrypt, "rb") as f:
            c_x = pickle.load(f)

        #not working properly, not sure how to check correspondence between decrypted and original text
        decrypted = dec(key_derived, c_x, v, set[2])

        #functions to update ciphertext
        tokens = tokgen(pk, sample_ternary_poly(set[1]), sample_ternary_poly(set[1]), set[2], 2.0, t)
        updated_ciphertext = upd(tokens, c_x, set[2])

    else:
        print("Invalid command, try again.")
