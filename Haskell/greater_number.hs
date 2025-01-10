nextIsGreater :: [Int] -> [Int]
nextIsGreater [] = []
nextIsGreater (x:numList) =
    let greaterList = [head numList] 
        previous = head numList
    in  x >= previous = greaterList ++ [x]
