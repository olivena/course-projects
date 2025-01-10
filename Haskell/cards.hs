credits :: (Char, Int) -> (Char, Int) -> Int
credits suit number = points
    where points (suit == ‘s’ && number == 14) = 14
          points = 0
